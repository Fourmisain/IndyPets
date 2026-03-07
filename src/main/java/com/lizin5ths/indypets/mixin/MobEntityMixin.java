package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.IndependentPet;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements IndependentPet {
	@Unique boolean indypets$isIndependent = false;
	@Unique BlockPos indypets$homePos;

	protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(
		method = "interact",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/mob/MobEntity;interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"
		),
		cancellable = true
	)
	public final void indypets$tryChangeFollowing(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir,
			@Share("isInteracting") LocalBooleanRef isInteracting, @Share("wasSitting") LocalBooleanRef wasSitting) {
		if (getEntityWorld().isClient() || !(player instanceof ServerPlayerEntity serverPlayer))
			return;

		if (hand == Hand.MAIN_HAND && canInteract(serverPlayer, this)) {
			if (player.isSneaking()) {
				if (sneakInteract(this, serverPlayer)) {
					// Note: This blocks interactMob() so it might conflict with other mods using sneak-interact
					cir.setReturnValue(ActionResult.SUCCESS);
				}
			} else {
				var config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());
				if (config.regularInteract) {
					// continue in method below
					isInteracting.set(true);
					wasSitting.set(isSitting(this));
				}
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(
		method = "interact",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/mob/MobEntity;interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
			shift = At.Shift.AFTER
		)
	)
	public final void indypets$tryChangeFollowingAfter(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir,
			@Share("isInteracting") LocalBooleanRef isInteracting, @Share("wasSitting") LocalBooleanRef wasSitting) {
		if (getEntityWorld().isClient() || !(player instanceof ServerPlayerEntity serverPlayer))
			return;

		if (isInteracting.get()) {
			if (wasSitting.get() != isSitting(this)) {
				// TODO this might have unwanted side effects with some mods
				// retroactively change state
				cycleState(wasSitting.get(), this);
				showPetStatus(serverPlayer, this, true);
			}
		}
	}

	@Inject(method = "writeCustomData", at = @At("HEAD"))
	private void indypets$writeFollowData(WriteView view, CallbackInfo ci) {
		if (!isSupported(this))
			return;

		view.putBoolean("AllowedToFollow", !indypets$isIndependent);
		if (indypets$homePos != null) {
			view.put("IndyPets$HomePos", BlockPos.CODEC, indypets$homePos);
		}
	}

	@Inject(method = "readCustomData", at = @At("TAIL"))
	private void indypets$readFollowData(ReadView view, CallbackInfo ci) {
		if (!isSupported(this))
			return;

		indypets$isIndependent = !view.getBoolean("AllowedToFollow", true);
		indypets$homePos = view.read("IndyPets$HomePos", BlockPos.CODEC)
			.orElseGet(this::getBlockPos); // fallback
	}

	@Unique @Override
	public boolean indypets$isIndependent() {
		return indypets$isIndependent;
	}

	@Unique @Override
	public void indypets$setIndependent(boolean value) {
		indypets$isIndependent = value;
	}

	@Unique @Override
	public BlockPos indypets$getHomePos() {
		return indypets$homePos;
	}

	@Unique @Override
	public void indypets$setHome() {
		indypets$homePos = getBlockPos();
		IndyPets.LOGGER.debug("set home of {}", this);
	}
}

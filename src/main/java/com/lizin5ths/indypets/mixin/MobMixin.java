package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.IndependentPet;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements IndependentPet {
	@Unique boolean indypets$isIndependent = false;
	@Unique BlockPos indypets$homePos;

	protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(
		method = "interact",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Mob;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"
		),
		cancellable = true
	)
	public final void indypets$tryChangeFollowing(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir,
			@Share("isInteracting") LocalBooleanRef isInteracting, @Share("wasSitting") LocalBooleanRef wasSitting) {
		if (level().isClientSide() || !(player instanceof ServerPlayer serverPlayer))
			return;

		if (hand == InteractionHand.MAIN_HAND && canInteract(serverPlayer, this)) {
			if (player.isShiftKeyDown()) {
				if (sneakInteract(this, serverPlayer)) {
					// Note: This blocks interactMob() so it might conflict with other mods using sneak-interact
					cir.setReturnValue(InteractionResult.SUCCESS);
				}
			} else {
				var config = ServerConfig.getDefaultedPlayerConfig(player.getUUID());
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
			target = "Lnet/minecraft/world/entity/Mob;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
			shift = At.Shift.AFTER
		)
	)
	public final void indypets$tryChangeFollowingAfter(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir,
			@Share("isInteracting") LocalBooleanRef isInteracting, @Share("wasSitting") LocalBooleanRef wasSitting) {
		if (level().isClientSide() || !(player instanceof ServerPlayer serverPlayer))
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

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void indypets$writeFollowData(ValueOutput view, CallbackInfo ci) {
		if (!isSupported(this))
			return;

		view.putBoolean("AllowedToFollow", !indypets$isIndependent);
		if (indypets$homePos != null) {
			view.store("IndyPets$HomePos", BlockPos.CODEC, indypets$homePos);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void indypets$readFollowData(ValueInput view, CallbackInfo ci) {
		if (!isSupported(this))
			return;

		indypets$isIndependent = !view.getBooleanOr("AllowedToFollow", true);
		indypets$homePos = view.read("IndyPets$HomePos", BlockPos.CODEC)
			.orElseGet(this::blockPosition); // fallback
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
		indypets$homePos = blockPosition();
		IndyPets.LOGGER.debug("set home of {}", this);
	}
}

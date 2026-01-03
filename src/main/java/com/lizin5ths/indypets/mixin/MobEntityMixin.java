package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.config.ServerConfig;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
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
		if (getWorld().isClient() || !(player instanceof ServerPlayerEntity serverPlayer))
			return;

		if (hand == Hand.MAIN_HAND && canInteract(serverPlayer, this)) {
			if (player.isSneaking()) {
				if (sneakInteract((TameableEntity) (Object) this, serverPlayer)) {
					// Note: This blocks interactMob() so it might conflict with other mods using sneak-interact
					cir.setReturnValue(ActionResult.success(true));
				}
			} else {
				var config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());
				if (config.regularInteract) {
					// continue in method below
					isInteracting.set(true);
					wasSitting.set(((TameableEntity) (Object) this).isSitting());
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
		if (getWorld().isClient() || !(player instanceof ServerPlayerEntity serverPlayer))
			return;

		if (isInteracting.get()) {
			TameableEntity tameable = (TameableEntity) (Object) this;

			if (wasSitting.get() != tameable.isSitting()) {
				// TODO this might have unwanted side effects with some mods
				// retroactively change state
				cycleState(wasSitting.get(), tameable);
				showPetStatus(serverPlayer, tameable, true);
			}
		}
	}
}

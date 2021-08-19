package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.util.IndyPetsUtil;
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

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
	protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "interact",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/mob/MobEntity;interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"),
		cancellable = true
	)
	public final void tryChangeFollowing(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (world.isClient) return;

		if ((Object) this instanceof TameableEntity) {
			TameableEntity self = (TameableEntity) (Object) this;

			if (player.isSneaking() && hand == Hand.MAIN_HAND) {
				if (IndyPetsUtil.changeFollowing((ServerPlayerEntity) player, self)) {
					// Note: This blocks interactMob() so it might conflict with other mods using sneak-interact
					cir.setReturnValue(ActionResult.success(true));
				}
			}
		}
	}
}

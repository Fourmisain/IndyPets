package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
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

	@Inject(method = "interact",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/mob/MobEntity;interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"),
		cancellable = true
	)
	public final void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (world.isClient) return;

		if ((Object) this instanceof TameableEntity) {
			TameableEntity self = (TameableEntity) (Object) this;

			if (hand == Hand.MAIN_HAND) {
				if (player.isSneaking()) {
					IndyPets.changeFollowing(player, self);
				}
			}
		}
	}
}

package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.IndyPetsUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
	public final void indypets$tryChangeFollowing(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (world.isClient) return;

		if ((Object) this instanceof TameableEntity) {
			TameableEntity self = (TameableEntity) (Object) this;
			Identifier id = EntityType.getId(self.getType());

			// don't interact with blocked pets
			Config config = ServerConfig.getDefaultedPlayerConfig(player.getUuid());
			if (config.blocklist.isBlocked(id))
				return;

			if (player.isSneaking() && hand == Hand.MAIN_HAND) {
				if (config.interactItem != null) {
					// only interact when holding the chosen item
					Identifier itemId = Registry.ITEM.getId(player.getMainHandStack().getItem());
					if (!itemId.equals(config.interactItem))
						return;
				}

				if (IndyPetsUtil.changeFollowing((ServerPlayerEntity) player, self)) {
					// Note: This blocks interactMob() so it might conflict with other mods using sneak-interact
					cir.setReturnValue(ActionResult.success(true));
				}
			}
		}
	}
}

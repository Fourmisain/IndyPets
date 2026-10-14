package com.lizin5ths.indypets.mixin.compat.followersteleporttoo;

import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

/** Compat for Followers Teleport Too >= 3.0 */
@Mixin(targets = "com.serilum.followersteleporttoo_common_fabric.events.TeleportEvent")
public abstract class TeleportEventV3Mixin {
	@Inject(method = "isFollowingOwner", at = @At("HEAD"), cancellable = true)
	private static void indypets$disableWhenIndependent(TameableEntity ta, ServerPlayerEntity player, String namespace, CallbackInfoReturnable<Boolean> cir) {
		if (isActiveIndependent(ta)) {
			cir.setReturnValue(false);
		}
	}
}

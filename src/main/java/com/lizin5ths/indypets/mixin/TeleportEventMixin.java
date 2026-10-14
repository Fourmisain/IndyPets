package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.util.IndyPetsUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** Compat for Followers Teleport Too <= 2.9 */
@Pseudo
@Mixin(targets = {
	"com.natamus.followersteleporttoo_common_fabric.events.TeleportEvent",
	"com.serilum.followersteleporttoo_common_fabric.events.TeleportEvent"
})
public abstract class TeleportEventMixin {
	@WrapOperation(method = "onPlayerTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/TameableEntity;isInSittingPose()Z"))
	private static boolean indypets$isSittingOrIndependent(TameableEntity tameable, Operation<Boolean> original) {
		return original.call(tameable) || IndyPetsUtil.isActiveIndependent(tameable);
	}
}

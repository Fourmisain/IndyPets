package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.util.IndyPetsUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.natamus.followersteleporttoo.events.TeleportEvent;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TeleportEvent.class)
public abstract class TeleportEventMixin {
	@WrapOperation(method = "onPlayerTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/TameableEntity;isInSittingPose()Z"))
	private static boolean indypets$isSittingOrIndependent(TameableEntity tameable, Operation<Boolean> original) {
		return original.call(tameable) || IndyPetsUtil.isActiveIndependent(tameable);
	}
}

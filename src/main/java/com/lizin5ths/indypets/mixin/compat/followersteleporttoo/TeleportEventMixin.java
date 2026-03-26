package com.lizin5ths.indypets.mixin.compat.followersteleporttoo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.natamus.followersteleporttoo_common_fabric.events.TeleportEvent;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

@Mixin(TeleportEvent.class)
public abstract class TeleportEventMixin {
	@WrapOperation(method = "onPlayerTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;isInSittingPose()Z"))
	private static boolean indypets$disableWhenIndependent(TamableAnimal tameable, Operation<Boolean> original) {
		return original.call(tameable) || isActiveIndependent(tameable);
	}
}

package com.lizin5ths.indypets.mixin.compat.followersteleporttoo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

/**
 * Compat for Followers Teleport Too <= 2.9 (pre-v3 API).
 * Targets both the pre-2.9 package (com.natamus.*) and the 2.9 package (com.serilum.*).
 * The injection point is the old onPlayerTeleport method that calls isInSittingPose().
 * @Pseudo + require=0 so a missing target/method does not crash startup on v3+ or when the mod is absent.
 */
@Pseudo
@Mixin(targets = {
	"com.natamus.followersteleporttoo_common_fabric.events.TeleportEvent",
	"com.serilum.followersteleporttoo_common_fabric.events.TeleportEvent"
})
public abstract class TeleportEventMixin {
	@WrapOperation(method = "onPlayerTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;isInSittingPose()Z"))
	private static boolean indypets$disableWhenIndependent(TamableAnimal tameable, Operation<Boolean> original) {
		return original.call(tameable) || isActiveIndependent(tameable);
	}
}

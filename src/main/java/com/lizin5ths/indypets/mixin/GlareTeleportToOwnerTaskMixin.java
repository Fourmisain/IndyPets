package com.lizin5ths.indypets.mixin;

import com.faboslav.friendsandfoes.entity.GlareEntity;
import com.faboslav.friendsandfoes.entity.ai.brain.task.glare.GlareTeleportToOwnerTask;
import com.lizin5ths.indypets.util.IndyPetsUtil;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlareTeleportToOwnerTask.class)
public abstract class GlareTeleportToOwnerTaskMixin {
	@Inject(method = "shouldRun", at = @At("HEAD"), cancellable = true, require = 0)
	public void indyPets$stopTeleporting(ServerWorld world, GlareEntity glare, CallbackInfoReturnable<Boolean> cir) {
		if (IndyPetsUtil.isIndependent(glare)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "run", at = @At("HEAD"), cancellable = true, require = 0)
	public void indyPets$doNotTeleport(ServerWorld world, GlareEntity glare, long time, CallbackInfo ci) {
		if (IndyPetsUtil.isIndependent(glare)) {
			ci.cancel();
		}
	}
}

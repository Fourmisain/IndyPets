package com.lizin5ths.indypets.mixin;

import com.faboslav.friendsandfoes.entity.GlareEntity;
import com.faboslav.friendsandfoes.entity.ai.goal.glare.GlareFollowOwnerGoal;
import com.lizin5ths.indypets.util.IndyPetsUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Friend's and Foes <2.0.8
@Mixin(GlareFollowOwnerGoal.class)
public abstract class GlareFollowOwnerGoalMixin {
	@Shadow(remap = false) @Final private GlareEntity glare;

	@Inject(method = {"canStart", "shouldContinue"}, at = @At("HEAD"), cancellable = true, require = 0)
	public void indyPets$stopFollowing(CallbackInfoReturnable<Boolean> cir) {
		if (IndyPetsUtil.isIndependent(glare)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 0)
	public void indyPets$doNotFollow(CallbackInfo ci) {
		if (IndyPetsUtil.isIndependent(glare)) {
			ci.cancel();
		}
	}
}

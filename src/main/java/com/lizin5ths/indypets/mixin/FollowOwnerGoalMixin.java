package com.lizin5ths.indypets.mixin;

import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

@Mixin(FollowOwnerGoal.class)
public abstract class FollowOwnerGoalMixin extends Goal {
	@Shadow @Final private TameableEntity tameable;

	@Inject(method = {"canStart", "shouldContinue"}, at = @At("HEAD"), cancellable = true)
	public void indyPets$stopFollowing(CallbackInfoReturnable<Boolean> cir) {
		if (isActiveIndependent(tameable)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void indyPets$doNotFollow(CallbackInfo ci) {
		if (isActiveIndependent(tameable)) {
			ci.cancel();
		}
	}
}

package com.lizin5ths.indypets.mixin.compat.suikefoxfriend;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import suike.suikefoxfriend.entity.ai.FoxFollowOwnerGoal;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

@Mixin(FoxFollowOwnerGoal.class)
public abstract class FoxFollowOwnerGoalMixin {
	@Shadow @Final private AnimalEntity fox;

	@Inject(method = {"canStart", "shouldContinue"}, at = @At("HEAD"), cancellable = true)
	public void indypets$stopFollowing(CallbackInfoReturnable<Boolean> cir) {
		if (isActiveIndependent(fox)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void indypets$doNotFollow(CallbackInfo ci) {
		if (isActiveIndependent(fox)) {
			ci.cancel();
		}
	}
}

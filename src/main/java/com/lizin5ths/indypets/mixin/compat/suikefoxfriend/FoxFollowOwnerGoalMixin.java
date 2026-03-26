package com.lizin5ths.indypets.mixin.compat.suikefoxfriend;

import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import suike.suikefoxfriend.entity.ai.FoxFollowOwnerGoal;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

import net.minecraft.world.entity.animal.fox.Fox;

@Mixin(FoxFollowOwnerGoal.class)
public abstract class FoxFollowOwnerGoalMixin {
	@Shadow @Final private Animal fox;

	@Inject(method = {"canUse", "canContinueToUse"}, at = @At("HEAD"), cancellable = true)
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

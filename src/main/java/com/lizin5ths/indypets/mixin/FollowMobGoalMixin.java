package com.lizin5ths.indypets.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;

@Mixin(FollowMobGoal.class)
public abstract class FollowMobGoalMixin {
	@Shadow @Final private Mob mob;

	@Inject(method = {"canUse", "canContinueToUse"}, at = @At("HEAD"), cancellable = true)
	public void indypets$stopFollowing(CallbackInfoReturnable<Boolean> cir) {
		if (shouldHeadHome(mob)) {
			cir.setReturnValue(false);
		}
	}
}

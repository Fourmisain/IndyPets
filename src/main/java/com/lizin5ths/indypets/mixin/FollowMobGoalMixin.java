package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.util.IndyPetsUtil;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FollowMobGoal.class)
public abstract class FollowMobGoalMixin {
	@Shadow @Final private MobEntity mob;

	@Inject(method = {"canStart", "shouldContinue"}, at = @At("HEAD"), cancellable = true)
	public void indyPets$stopFollowing(CallbackInfoReturnable<Boolean> cir) {
		if (IndyPetsUtil.shouldHeadHome(mob)) {
			cir.setReturnValue(false);
		}
	}
}

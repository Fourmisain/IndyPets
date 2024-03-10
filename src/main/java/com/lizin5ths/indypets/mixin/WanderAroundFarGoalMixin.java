package com.lizin5ths.indypets.mixin;

import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.findTowardsHome;
import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

@Mixin(WanderAroundFarGoal.class)
public abstract class WanderAroundFarGoalMixin extends WanderAroundGoal {
	public WanderAroundFarGoalMixin(PathAwareEntity mob, double speed) {
		super(mob, speed);
	}

	@Inject(method = "getWanderTarget", at = @At("HEAD"), cancellable = true)
	protected void indypets$dontStrayFromHome(CallbackInfoReturnable<Vec3d> cir) {
		if (shouldHeadHome(mob)) {
			cir.setReturnValue(findTowardsHome(mob));
		}
	}
}

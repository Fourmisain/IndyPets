package com.lizin5ths.indypets.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.findTowardsHome;
import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.phys.Vec3;

@Mixin(WaterAvoidingRandomStrollGoal.class)
public abstract class WanderAroundFarGoalMixin extends RandomStrollGoal {
	public WanderAroundFarGoalMixin(PathfinderMob mob, double speed) {
		super(mob, speed);
	}

	@Inject(method = "getPosition", at = @At("HEAD"), cancellable = true)
	protected void indypets$dontStrayFromHome(CallbackInfoReturnable<Vec3> cir) {
		if (shouldHeadHome(mob)) {
			cir.setReturnValue(findTowardsHome(mob));
		}
	}
}

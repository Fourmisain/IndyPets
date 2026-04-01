package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.mixin.access.RandomStrollGoalAccessor;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;

@Mixin(RandomStrollGoal.class)
public abstract class RandomStrollGoalMixin {
	@Shadow @Final
	protected PathfinderMob mob;

	@Inject(method = "canUse", at = @At("HEAD"))
	public void indypets$countTamedPetsAsPersistent(CallbackInfoReturnable<Boolean> cir) {
		// RandomStrollGoal will only start if the mob's despawn timer isn't too high or if the mob is persistent
		// Tamed cats are persistent but wolves are not (despite them not despawning), hence they will stop
		// wandering around after some time and only start moving again once their despawn timer resets (by a player getting near them)

		// This will override the timer check for all tamed mobs, making them wander around even without player presence
		if (isSupported(mob) && isTamed(mob)) {
			((RandomStrollGoalAccessor) this).setCheckNoActionTime(false);
		}
	}

	// vanilla pets only have a 1 in 1000 chance to use this, but just in case we shall too (using NoPenaltyTargeting too)
	@Inject(method = "getPosition", at = @At("HEAD"), cancellable = true)
	protected void indypets$dontStrayFromHome(CallbackInfoReturnable<Vec3> cir) {
		if (shouldHeadHome(mob)) {
			cir.setReturnValue(findTowardsHome(mob, true));
		}
	}
}

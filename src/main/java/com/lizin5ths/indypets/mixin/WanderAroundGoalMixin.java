package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.mixin.access.WanderAroundGoalAccessor;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lizin5ths.indypets.util.IndyPetsUtil.findTowardsHome;
import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

@Mixin(WanderAroundGoal.class)
public abstract class WanderAroundGoalMixin {
	@Shadow @Final
	protected PathAwareEntity mob;

	@Inject(method = "canStart", at = @At("HEAD"))
	public void indypets$countTamedPetsAsPersistent(CallbackInfoReturnable<Boolean> cir) {
		// WanderAroundGoal will only start if the mobs despawn timer isn't too high or if the mob is persistent
		// Tamed cats are persistent but wolves are not persistent (despite them not despawning), hence they will actually
		// stop wandering around after some time and only start moving again once their despawn timer resets (by a player getting near them)

		// This will override the timer check for all tambed mobs, making them actually wander around even without player presence
		if (mob instanceof TameableEntity && ((TameableEntity) mob).isTamed()) {
			((WanderAroundGoalAccessor) this).setCanDespawn(false);
		}
	}

	// vanilla pets only have a 1 in 1000 chance to use this, but just in case we shall too (using NoPenaltyTargeting too)
	@Inject(method = "getWanderTarget", at = @At("HEAD"), cancellable = true)
	protected void indypets$dontStrayFromHome(CallbackInfoReturnable<Vec3d> cir) {
		if (shouldHeadHome(mob)) {
			cir.setReturnValue(findTowardsHome(mob, true));
		}
	}
}

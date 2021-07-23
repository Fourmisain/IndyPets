package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.Follower;
import com.lizin5ths.indypets.IndyPets;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FollowOwnerGoal.class)
public abstract class FollowOwnerGoalMixin extends Goal {
	@Shadow @Final private TameableEntity tameable;
	@Shadow private int updateCountdownTicks;

	@Unique
	private static boolean hasToggles(TameableEntity pet) {
		return pet instanceof CatEntity || pet instanceof ParrotEntity || pet instanceof WolfEntity;
	}

	@Unique
	private static boolean hasTogglesAndForbidsFollowing(TameableEntity pet) {
		return (pet instanceof CatEntity && IndyPets.CONFIG.disableCatFollow) || (pet instanceof ParrotEntity && IndyPets.CONFIG.disableParrotFollow) || (pet instanceof WolfEntity && IndyPets.CONFIG.disableWolfFollow);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void delayTick(CallbackInfo ci) {
		Follower follower = (Follower) tameable;

		if (IndyPets.CONFIG.selectiveFollowing) {
			// In selective following mode, handle each pet separately
			if (!follower.isFollowing()) {
				updateCountdownTicks = 10; // don't follow / teleport to the owner
			}
		} else if (!hasToggles(tameable) || hasTogglesAndForbidsFollowing(tameable)) {
			// Without selective following mode, don't follow / teleport to the owner
			// unless the corresponding "disable...follow" switch is turned off
			updateCountdownTicks = 10;
		}
	}
}
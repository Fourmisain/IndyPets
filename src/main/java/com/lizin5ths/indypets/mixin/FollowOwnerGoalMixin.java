package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.Follower;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FollowOwnerGoal.class)
public abstract class FollowOwnerGoalMixin extends Goal {
	@Shadow @Final private TameableEntity tameable;
	@Shadow private int updateCountdownTicks;

	@Inject(method = "tick", at = @At("HEAD"))
	public void delayTick(CallbackInfo ci) {
		Follower follower = (Follower) tameable;

		Config config = ServerConfig.getPlayerConfig(tameable.getOwnerUuid());

		if (config.selectiveFollowing) {
			// In selective following mode, handle each pet separately
			if (!follower.isFollowing()) {
				updateCountdownTicks = 10; // don't follow / teleport to the owner
			}
		} else if (config.getDefaultIndependence(tameable)) {
			// Without selective following mode, don't follow / teleport to
			// the owner unless it was disabled for the pet type
			updateCountdownTicks = 10;
		}
	}
}
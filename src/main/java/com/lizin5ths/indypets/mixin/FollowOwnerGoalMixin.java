package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.Follower;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.Identifier;
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
		Identifier id = EntityType.getId(tameable.getType());

		Config config = ServerConfig.getDefaultedPlayerConfig(tameable.getOwnerUuid());
		if (config.blocklist.isBlocked(id))
			return;

		if (!((Follower) tameable).isFollowing()) {
			updateCountdownTicks = 10; // don't follow / teleport to the owner
		}
	}
}

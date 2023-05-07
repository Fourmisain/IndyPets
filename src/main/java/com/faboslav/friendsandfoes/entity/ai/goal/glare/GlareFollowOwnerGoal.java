package com.faboslav.friendsandfoes.entity.ai.goal.glare;

import com.faboslav.friendsandfoes.entity.GlareEntity;
import net.minecraft.entity.ai.goal.Goal;

public class GlareFollowOwnerGoal extends Goal {
	private final GlareEntity glare = null;

	@Override
	public boolean canStart() {
		return false;
	}

	@Override
	public boolean shouldContinue() {
		return false;
	}

	@Override
	public void tick() {

	}
}

package com.faboslav.friendsandfoes.entity.ai.brain.task.glare;

import com.faboslav.friendsandfoes.entity.GlareEntity;
import net.minecraft.server.world.ServerWorld;

public class GlareTeleportToOwnerTask {
	protected boolean shouldRun(ServerWorld world, GlareEntity glare) {
		return false;
	}

	protected void run(ServerWorld world, GlareEntity glare, long time) {

	}
}

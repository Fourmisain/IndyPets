package com.faboslav.friendsandfoes.entity.ai.brain.task.glare;

import com.faboslav.friendsandfoes.entity.GlareEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class GlareTeleportToOwnerTask extends MultiTickTask<GlareEntity> {
	public GlareTeleportToOwnerTask(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState) {
		super(requiredMemoryState);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, GlareEntity glare) {
		return false;
	}

	@Override
	protected void run(ServerWorld world, GlareEntity glare, long time) {

	}
}

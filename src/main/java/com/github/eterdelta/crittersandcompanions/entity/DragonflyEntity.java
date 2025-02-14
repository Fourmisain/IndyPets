package com.github.eterdelta.crittersandcompanions.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;

public abstract class DragonflyEntity extends TameableEntity {
	protected DragonflyEntity(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}

	public class RandomFlyGoal extends Goal {
		@Override
		public boolean canStart() {
			return false;
		}

		@Override
		public void start() {
			DragonflyEntity.this.getRotationVec(0.0F);
		}
	}
}
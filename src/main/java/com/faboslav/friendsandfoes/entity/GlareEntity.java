package com.faboslav.friendsandfoes.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlareEntity extends TameableEntity {
	protected GlareEntity(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null;
	}
}

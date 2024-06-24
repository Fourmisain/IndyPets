package com.faboslav.friendsandfoes.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;

public abstract class GlareEntity extends TameableEntity {
	protected GlareEntity(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}
}

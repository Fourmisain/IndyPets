package com.lizin5ths.indypets.mixin.vanilla;

import com.lizin5ths.indypets.util.IndyPetsUtil;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin extends TameableEntity {
	protected CatEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}

	@WrapWithCondition(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/CatEntity;setSitting(Z)V"))
	public boolean indypets$cycleState(CatEntity tameable, boolean isNotSitting, @Local(argsOnly = true) PlayerEntity player) {
		if (getWorld().isClient())
			return true;

		return IndyPetsUtil.cycleState(tameable, (ServerPlayerEntity) player);
	}
}

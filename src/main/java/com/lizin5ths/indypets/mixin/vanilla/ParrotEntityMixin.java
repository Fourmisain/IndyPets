package com.lizin5ths.indypets.mixin.vanilla;

import com.lizin5ths.indypets.util.IndyPetsUtil;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParrotEntity.class)
public abstract class ParrotEntityMixin extends TameableEntity {
	protected ParrotEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}

	@WrapWithCondition(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/ParrotEntity;setSitting(Z)V"))
	public boolean indypets$cycleState(ParrotEntity tameable, boolean isNotSitting, @Local(argsOnly = true) PlayerEntity player) {
		return IndyPetsUtil.cycleState(tameable, (ServerPlayerEntity) player);
	}
}

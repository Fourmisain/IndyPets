package com.lizin5ths.indypets.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.lizin5ths.indypets.util.IndyPetsUtil.getHomePos;
import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

@Mixin(WaterAvoidingRandomFlyingGoal.class)
public abstract class WaterAvoidingRandomFlyingGoalMixin extends WaterAvoidingRandomStrollGoal {
	public WaterAvoidingRandomFlyingGoalMixin(PathfinderMob pathAwareEntity, double d) {
		super(pathAwareEntity, d);
	}

	@ModifyExpressionValue(method = "getPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PathfinderMob;getViewVector(F)Lnet/minecraft/world/phys/Vec3;"))
	protected Vec3 indypets$dontStrayFromHome(Vec3 original) {
		if (shouldHeadHome(mob)) {
			BlockPos homePos = getHomePos(mob);

			return homePos.getCenter().subtract(mob.position());
		}

		return original;
	}
}

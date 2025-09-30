package com.lizin5ths.indypets.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.lizin5ths.indypets.util.IndyPetsUtil.getHomePos;
import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

@Mixin(FlyGoal.class)
public abstract class FlyGoalMixin extends WanderAroundFarGoal {
	public FlyGoalMixin(PathAwareEntity pathAwareEntity, double d) {
		super(pathAwareEntity, d);
	}

	@ModifyExpressionValue(method = "getWanderTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/PathAwareEntity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"))
	protected Vec3d indypets$dontStrayFromHome(Vec3d original) {
		if (shouldHeadHome(mob)) {
			BlockPos homePos = getHomePos((TameableEntity) mob);

			return homePos.toCenterPos().subtract(mob.getEntityPos());
		}

		return original;
	}
}

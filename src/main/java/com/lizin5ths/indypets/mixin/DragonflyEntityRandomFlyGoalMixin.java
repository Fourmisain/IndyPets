package com.lizin5ths.indypets.mixin;

import com.github.eterdelta.crittersandcompanions.entity.DragonflyEntity;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.entity.ai.pathing.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.lizin5ths.indypets.util.IndyPetsUtil.getHomePos;
import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

@Mixin(DragonflyEntity.RandomFlyGoal.class)
public abstract class DragonflyEntityRandomFlyGoalMixin {
	@ModifyReceiver(
		method = "start",
		at = @At(
			value = "INVOKE",
			target = "Lcom/github/eterdelta/crittersandcompanions/entity/DragonflyEntity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"
		)
	)
	public DragonflyEntity indypets$captureThis(DragonflyEntity entity, float tickDelta, @Cancellable CallbackInfo ci) {
		if (shouldHeadHome(entity)) {
			Path pathHome = entity.getNavigation().findPathTo(getHomePos(entity), 1, 14);
			entity.getNavigation().startMovingAlong(pathHome, 1);
			ci.cancel();
		}

		return entity;
	}
}

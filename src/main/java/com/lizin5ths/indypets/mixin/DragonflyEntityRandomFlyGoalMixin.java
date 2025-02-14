package com.lizin5ths.indypets.mixin;

import com.github.eterdelta.crittersandcompanions.entity.DragonflyEntity;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.lizin5ths.indypets.util.IndyPetsUtil.getHomePos;
import static com.lizin5ths.indypets.util.IndyPetsUtil.shouldHeadHome;

@Mixin(DragonflyEntity.RandomFlyGoal.class)
public abstract class DragonflyEntityRandomFlyGoalMixin {
	@ModifyReceiver(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lcom/github/eterdelta/crittersandcompanions/entity/DragonflyEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"
		)
	)
	public DragonflyEntity indypets$captureThis(DragonflyEntity entity, EntityAttribute attribute, @Cancellable CallbackInfo ci) {
		if (entity.getNavigation().isFollowingPath()) {
			ci.cancel();
		} else if (shouldHeadHome(entity)) {
			Path pathHome = entity.getNavigation().findPathTo(getHomePos(entity), 1, 14);
			entity.getNavigation().startMovingAlong(pathHome, 1);
			ci.cancel();
		}

		return entity;
	}
}

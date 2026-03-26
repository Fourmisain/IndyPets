package com.lizin5ths.indypets.mixin.compat.friendsandfoes;

import com.faboslav.friendsandfoes.common.entity.ai.brain.GlareBrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

@Mixin(GlareBrain.class)
public abstract class GlareBrainMixin {
	@ModifyArg(
		method = "addIdleActivities",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/ai/behavior/StayCloseToTarget;create(Ljava/util/function/Function;Ljava/util/function/Predicate;IIF)Lnet/minecraft/world/entity/ai/behavior/BehaviorControl;"
		),
		index = 1,
		require = 0
	)
	private static Predicate<LivingEntity> indypets$dontFollowWhenIndependent(Predicate<LivingEntity> predicate) {
		return glare -> {
			if (isActiveIndependent(glare)) {
				// to stop the current movement, the brain needs to forget walk/look targets
				// this should only be done once when the pet is set independent, hence this is done in IndyPetsUtil.changeFollowing
				return false;
			}

			return predicate.test(glare);
		};
	}
}

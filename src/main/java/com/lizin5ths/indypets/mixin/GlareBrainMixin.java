package com.lizin5ths.indypets.mixin;

import com.faboslav.friendsandfoes.common.entity.GlareEntity;
import com.faboslav.friendsandfoes.common.entity.ai.brain.GlareBrain;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LookTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;
import java.util.function.Function;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

@Mixin(GlareBrain.class)
public abstract class GlareBrainMixin {
	@ModifyArg(
		method = "addIdleActivities",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/ai/brain/task/WalkTowardsLookTargetTask;<init>(Ljava/util/function/Function;IIF)V"
		),
		index = 0,
		require = 0
	)
	private static Function<LivingEntity, Optional<LookTarget>> indyPets$dontFollowWhenIndependent(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, @Local(ordinal = 0, argsOnly = true) Brain<GlareEntity> brain) {
		return glare -> {
			if (isActiveIndependent(glare)) {
				// to stop the current movement, the brain needs to forget walk/look targets
				// this should only be done once when the pet is set independent, hence this is done in IndyPetsUtil.changeFollowing
				return Optional.empty();
			}

			return lookTargetFunction.apply(glare);
		};
	}
}

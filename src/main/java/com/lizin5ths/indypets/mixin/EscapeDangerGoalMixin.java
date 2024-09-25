package com.lizin5ths.indypets.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isIndependent;

@Mixin(EscapeDangerGoal.class)
public abstract class EscapeDangerGoalMixin {
	@Shadow @Final
	protected PathAwareEntity mob;

	@ModifyReturnValue(method = "canStart", at = @At("RETURN"))
	public boolean indypets$petIsAboutToDrown(boolean original) {
		if (mob instanceof TameableEntity && isIndependent((TameableEntity) mob) && mob.getAir() <= 100) {
			return true;
		}

		return original;
	}
}

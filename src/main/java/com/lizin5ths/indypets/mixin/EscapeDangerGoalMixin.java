package com.lizin5ths.indypets.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isIndependent;
import static com.lizin5ths.indypets.util.IndyPetsUtil.isSupported;

@Mixin(EscapeDangerGoal.class)
public abstract class EscapeDangerGoalMixin {
	@Shadow @Final
	protected PathAwareEntity mob;

	@ModifyReturnValue(method = "isInDanger", at = @At("RETURN"))
	public boolean indypets$petIsAboutToDrown(boolean original) {
		if (isSupported(mob) && isIndependent(mob) && mob.getAir() <= 100) {
			return true;
		}

		return original;
	}
}

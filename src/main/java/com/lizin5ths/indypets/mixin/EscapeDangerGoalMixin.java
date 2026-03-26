package com.lizin5ths.indypets.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isIndependent;
import static com.lizin5ths.indypets.util.IndyPetsUtil.isSupported;

@Mixin(PanicGoal.class)
public abstract class EscapeDangerGoalMixin {
	@Shadow @Final
	protected PathfinderMob mob;

	@ModifyReturnValue(method = "shouldPanic", at = @At("RETURN"))
	public boolean indypets$petIsAboutToDrown(boolean original) {
		if (isSupported(mob) && isIndependent(mob) && mob.getAirSupply() <= 100) {
			return true;
		}

		return original;
	}
}

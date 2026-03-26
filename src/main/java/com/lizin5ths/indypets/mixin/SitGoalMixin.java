package com.lizin5ths.indypets.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;

@Mixin(SitWhenOrderedToGoal.class)
public abstract class SitGoalMixin {
	@Shadow @Final
	private TamableAnimal mob;

	@ModifyConstant(
		method = "canUse",
		constant = @Constant(intValue = 1) // the true inside owner == null
	)
	public int indypets$dontSitOnLogoutWhenIndependent(int canStart) {
		if (!mob.isOrderedToSit() && isActiveIndependent(mob))
			return 0;

		return canStart;
	}
}

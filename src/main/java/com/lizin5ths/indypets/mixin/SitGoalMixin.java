package com.lizin5ths.indypets.mixin;

import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

@Mixin(SitGoal.class)
public abstract class SitGoalMixin {
	@Shadow @Final
	private TameableEntity tameable;

	@ModifyConstant(
		method = "canStart",
		constant = @Constant(intValue = 1) // the true inside owner == null
	)
	public int indypets$dontSitOnLogoutWhenIndependent(int canStart) {
		if (!tameable.isSitting() && isActiveIndependent(tameable))
			return 0;

		return canStart;
	}
}

package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.util.Follower;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SitGoal.class)
public abstract class SitGoalMixin {
    @Shadow @Final
    private TameableEntity tameable;

    @ModifyConstant(
        method = "canStart",
        constant = @Constant(intValue = 1) // the true inside owner == null
    )
    public int indypets$dontSitOnLogoutWhenIndependent(int canStart) {
        Follower follower = (Follower) tameable;

        if (!follower.isFollowing())
            return 0;

        return canStart;
    }
}

package com.lizin5ths.indypets.mixin;

import net.minecraft.entity.ai.goal.WanderAroundGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WanderAroundGoal.class)
public interface WanderAroundGoalAccessor {
    @Mutable @Accessor
    void setCanDespawn(boolean value);
}

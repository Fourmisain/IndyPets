package com.lizin5ths.indypets.mixin;

import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WanderAroundGoal.class)
public abstract class WanderAroundGoalMixin {
    @Shadow @Final
    protected PathAwareEntity mob;

    @Inject(method = "canStart", at = @At("HEAD"))
    public void countTamedPetsAsPersistent(CallbackInfoReturnable<Boolean> cir) {
        // WanderAroundGoal will only start if the mobs despawn timer isn't too high or if the mob is persistent
        // Tamed cats are persistent but wolves are not persistent (despite them not despawning), hence they will actually
        // stop wandering around after some time and only start moving again once their despawn timer resets (by a player getting near them)

        // This will override the timer check for all tambed mobs, making them actually wander around even without player presence
        if (mob instanceof TameableEntity && ((TameableEntity) mob).isTamed()) {
            ((WanderAroundGoalAccessor) this).setCanDespawn(false);
        }
    }
}

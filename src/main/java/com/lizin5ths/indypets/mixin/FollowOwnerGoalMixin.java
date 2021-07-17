package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.AllowedToFollowAccessor;
import com.lizin5ths.indypets.IndyPetsConfig;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FollowOwnerGoal.class)
public abstract class FollowOwnerGoalMixin extends Goal {
    @Shadow @Final private TameableEntity tameable;
    @Shadow private int updateCountdownTicks;

    public boolean hasToggles(TameableEntity pet) {
        return pet instanceof CatEntity || pet instanceof ParrotEntity || pet instanceof WolfEntity;
    }

    public boolean hasTogglesAndForbidsFollowing(TameableEntity pet) {
        return (pet instanceof CatEntity && IndyPetsConfig.getDisableCatFollow()) || (pet instanceof ParrotEntity && IndyPetsConfig.getDisableParrotFollow()) || (pet instanceof WolfEntity && IndyPetsConfig.getDisableWolfFollow());
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void delayTick(CallbackInfo ci){
        if (IndyPetsConfig.getSelectiveFollowing()) {
            // Selective Following on. If not allowed to follow+teleport, delay tick.
            if (!((AllowedToFollowAccessor) this.tameable).getAllowedToFollow()) {
                this.updateCountdownTicks = 10;
            }
        } else if (!hasToggles(this.tameable) || hasTogglesAndForbidsFollowing(this.tameable)) {
            // Selective Following off, delay tick unless a supported pet type allows follow+teleport.
            this.updateCountdownTicks = 10;
        }
    }
}

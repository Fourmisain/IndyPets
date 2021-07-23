package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.AllowedToFollowAccessor;
import com.lizin5ths.indypets.IndyPetsConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntity implements AllowedToFollowAccessor {
    private static TrackedData<Boolean> ALLOWED_TO_FOLLOW;

    // Required to compile:
    protected TameableEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    // These three injects just add similar code for tracking selective following.
    @Inject(at = @At("TAIL"), method = "initDataTracker")
    private void injectInitFollowData(CallbackInfo callbackInfo) {
        TameableEntity self = (TameableEntity)(Object)this;
        self.getDataTracker().startTracking(ALLOWED_TO_FOLLOW, false);
    }
    @Inject(at = @At("TAIL"), method = "writeCustomDataToTag")
    private void injectFollowDataToTag(CompoundTag tag, CallbackInfo callbackInfo) {
        tag.putBoolean("AllowedToFollow", this.getAllowedToFollow());
    }
    @Inject(at = @At("TAIL"), method = "readCustomDataFromTag")
    private void injectFollowDataFromTag(CompoundTag tag, CallbackInfo callbackInfo) {
        this.setAllowedToFollow(tag.getBoolean("AllowedToFollow"));
    }

    // Using AllowedToFollowAccessor to pass this function to FollowOwnerGoalMixin.
    @Override
    public boolean getAllowedToFollow() {
        TameableEntity self = (TameableEntity)(Object)this;
        return self.getDataTracker().get(ALLOWED_TO_FOLLOW);
    }

    public void setAllowedToFollow(boolean value) {
        TameableEntity self = (TameableEntity)(Object)this;
        self.getDataTracker().set(ALLOWED_TO_FOLLOW, value);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        TameableEntity self = (TameableEntity)(Object)this;
        if (IndyPetsConfig.getSelectiveFollowing() && self.isOwner(player) && player.isSneaking()) {
            // Selective Following on, allow toggling behavior while sneaking for owned pets.
            if (this.getAllowedToFollow()) {
                // Forbid follow+teleport for target.
                this.setAllowedToFollow(false);
                if (!IndyPetsConfig.getSilentMode()) {
                    player.sendSystemMessage(new LiteralText("(IndyPets) Follow+teleport now forbidden for target."), Util.NIL_UUID);
                }
            } else {
                // Allow follow+teleport for target.
                this.setAllowedToFollow(true);
                if (!IndyPetsConfig.getSilentMode()) {
                    player.sendSystemMessage(new LiteralText("(IndyPets) Follow+teleport now allowed for target."), Util.NIL_UUID);
                }
            }
        }
        return super.interactMob(player, hand);
    }

    // Add to the static block at the end of TameableEntity.
    @Inject(at = @At("TAIL"), method = "<clinit>")
    static private void injectStatic(CallbackInfo callbackInfo) {
        ALLOWED_TO_FOLLOW = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
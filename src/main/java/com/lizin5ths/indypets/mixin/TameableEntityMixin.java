package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.Follower;
import com.lizin5ths.indypets.config.Config;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntity implements Follower {
	@Unique
	boolean isFollowing;

	protected TameableEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "onTamedChanged", at = @At(value = "HEAD"))
	protected void initFollowData(CallbackInfo ci) {
		isFollowing = !Config.CONFIG.getDefaultIndependence((TameableEntity) (Object) this);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void writeFollowDataToNbt(NbtCompound nbt, CallbackInfo callbackInfo) {
		nbt.putBoolean("AllowedToFollow", isFollowing);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void readFollowDataFromNbt(NbtCompound nbt, CallbackInfo callbackInfo) {
		isFollowing = nbt.getBoolean("AllowedToFollow");
	}

	@Unique
	@Override
	public boolean isFollowing() {
		return isFollowing;
	}

	@Unique
	@Override
	public void setFollowing(boolean value) {
		isFollowing = value;
	}
}
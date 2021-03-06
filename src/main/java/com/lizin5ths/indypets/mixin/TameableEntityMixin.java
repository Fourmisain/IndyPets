package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.Follower;
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

	@Inject(method = "setOwnerUuid", at = @At(value = "TAIL"))
	protected void initFollowData(CallbackInfo ci) {
		if (world.isClient) return;

		TameableEntity self = (TameableEntity) (Object) this;

		// only set state if it was player-caused
		Config config = ServerConfig.PLAYER_CONFIG.get(self.getOwnerUuid());
		if (config != null) {
			isFollowing = !config.getDefaultIndependence(self);
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
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

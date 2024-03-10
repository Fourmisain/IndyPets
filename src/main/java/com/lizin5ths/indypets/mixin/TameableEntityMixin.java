package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.Follower;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntity implements Follower {
	@Shadow public abstract boolean isTamed();

	@Unique boolean indypets$isFollowing;
	@Unique BlockPos indypets$homePos;

	protected TameableEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "setOwnerUuid", at = @At(value = "TAIL"))
	protected void indypets$initFollowData(CallbackInfo ci) {
		if (world.isClient) return;

		TameableEntity self = (TameableEntity) (Object) this;

		indypets$isFollowing = !ServerConfig.getDefaultedPlayerConfig(self.getOwnerUuid()).getDefaultIndependence(self);
		indypets$setHome();
	}

	@Unique
	private static NbtList toNbtList(int... values) {
		NbtList nbtList = new NbtList();

		for (int i : values) {
			nbtList.add(NbtInt.of(i));
		}

		return nbtList;
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
	private void indypets$writeFollowDataToNbt(NbtCompound nbt, CallbackInfo callbackInfo) {
		nbt.putBoolean("AllowedToFollow", indypets$isFollowing);

		if (indypets$homePos != null) {
			nbt.put("IndyPets$HomePos", toNbtList(indypets$homePos.getX(), indypets$homePos.getY(), indypets$homePos.getZ()));
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void indypets$readFollowDataFromNbt(NbtCompound nbt, CallbackInfo callbackInfo) {
		indypets$isFollowing = nbt.getBoolean("AllowedToFollow");

		if (nbt.contains("IndyPets$HomePos", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("IndyPets$HomePos", NbtElement.INT_TYPE);
			indypets$homePos = new BlockPos(nbtList.getInt(0), nbtList.getInt(1), nbtList.getInt(2));
		} else if (isTamed()) {
			indypets$setHome(); // fallback
		}
	}

	@Unique
	@Override
	public boolean isFollowing() {
		return indypets$isFollowing;
	}

	@Unique
	@Override
	public void setFollowing(boolean value) {
		indypets$isFollowing = value;
		if (!value) {
			indypets$setHome();
		}
	}

	@Unique
	public void indypets$setHome() {
		indypets$homePos = getBlockPos();
	}

	@Unique
	@Override
	public BlockPos getHomePos() {
		return indypets$homePos;
	}
}

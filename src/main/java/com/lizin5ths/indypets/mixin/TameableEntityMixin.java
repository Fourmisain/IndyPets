package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.util.Independence;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntity implements Independence {
	@Shadow public abstract boolean isTamed();

	@Unique boolean indypets$isIndependent = false;
	@Unique BlockPos indypets$homePos;

	protected TameableEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow @Nullable
	public UUID getOwnerUuid() { throw new AssertionError(); }

	@Inject(method = "setOwnerUuid", at = @At(value = "HEAD"))
	protected void indypets$capturePreviousOwner(CallbackInfo ci, @Share("owner") LocalRef<UUID> owner) {
		if (getWorld().isClient())
			return;

		owner.set(getOwnerUuid());
	}

	// in 1.21.4 and below, this will be called from readCustomDataFromNbt, which is no issue since we're going to
	// overwrite isIndependent and homePos later
	@Inject(method = "setOwnerUuid", at = @At(value = "TAIL"))
	protected void indypets$initFollowData(CallbackInfo ci, @Share("owner") LocalRef<UUID> owner) {
		if (getWorld().isClient)
			return;

		var oldUuid = owner.get();
		var newUuid = getOwnerUuid();

		if (!Objects.equals(oldUuid, newUuid)) {
			IndyPets.LOGGER.debug("{} changed owner from {} to {}", this, oldUuid, newUuid);
			indypets$isIndependent = false;
			indypets$setHome();
		}
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
		nbt.putBoolean("AllowedToFollow", !indypets$isIndependent);

		if (indypets$homePos != null) {
			nbt.put("IndyPets$HomePos", toNbtList(indypets$homePos.getX(), indypets$homePos.getY(), indypets$homePos.getZ()));
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void indypets$readFollowDataFromNbt(NbtCompound nbt, CallbackInfo callbackInfo) {
		if (nbt.contains("AllowedToFollow")) {
			indypets$isIndependent = !nbt.getBoolean("AllowedToFollow");
		} else {
			indypets$isIndependent = false;
		}

		if (nbt.contains("IndyPets$HomePos", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("IndyPets$HomePos", NbtElement.INT_TYPE);
			indypets$homePos = new BlockPos(nbtList.getInt(0), nbtList.getInt(1), nbtList.getInt(2));
		} else if (isTamed()) {
			indypets$setHome(); // fallback
		}
	}

	@Unique @Override
	public boolean indypets$isIndependent() {
		return indypets$isIndependent;
	}

	@Unique @Override
	public void indypets$toggleIndependence() {
		indypets$isIndependent = !indypets$isIndependent;
		if (indypets$isIndependent) {
			indypets$setHome();
		}
	}

	@Unique @Override
	public BlockPos indypets$getHomePos() {
		return indypets$homePos;
	}

	@Unique @Override
	public void indypets$setHome() {
		indypets$homePos = getBlockPos();
	}
}

package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.util.Independence;
import java.util.Optional;
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
public abstract class TameableEntityMixin extends AnimalEntity implements Independence {
	@Shadow public abstract boolean isTamed();

	@Unique boolean indypets$isIndependent = false;
	@Unique BlockPos indypets$homePos;

	protected TameableEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = {
		"setOwner(Lnet/minecraft/entity/LivingEntity;)V",
		"setOwner(Lnet/minecraft/entity/LazyEntityReference;)V"
	}, at = @At(value = "TAIL"))
	protected void indypets$initFollowData(CallbackInfo ci) {
		if (getWorld().isClient())
			return;

		indypets$isIndependent = false;
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
		nbt.putBoolean("AllowedToFollow", !indypets$isIndependent);

		if (indypets$homePos != null) {
			nbt.put("IndyPets$HomePos", toNbtList(indypets$homePos.getX(), indypets$homePos.getY(), indypets$homePos.getZ()));
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void indypets$readFollowDataFromNbt(NbtCompound nbt, CallbackInfo callbackInfo) {
		indypets$isIndependent = !nbt.getBoolean("AllowedToFollow").orElse(false);

		Optional<NbtList> nbtList = nbt.getList("IndyPets$HomePos");
		if (nbtList.isPresent()) {
			NbtList homePos = nbtList.get();
			indypets$homePos = new BlockPos(homePos.getInt(0).orElseThrow(), homePos.getInt(1).orElseThrow(), homePos.getInt(2).orElseThrow());
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

	@Unique
	public void indypets$setHome() {
		indypets$homePos = getBlockPos();
	}
}

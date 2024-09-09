package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.util.Independence;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.lizin5ths.indypets.util.IndyPetsUtil.cycleState;
import static com.lizin5ths.indypets.util.IndyPetsUtil.showPetStatus;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntity implements Independence {
	@Shadow public abstract boolean isTamed();
	@Shadow private boolean sitting;

	@Unique boolean indypets$isIndependent = false;
	@Unique BlockPos indypets$homePos;

	protected TameableEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyVariable(method = "setSitting", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private boolean indypets$tryCycleState(boolean value, @Share("cycling") LocalBooleanRef cycling) {
		if (world.isClient())
			return value;

		if (IndyPets.interactingPlayer.get() != null) {
			if (value == sitting) {
				IndyPets.LOGGER.warn("unexpected setSitting call");
				return value;
			}

			cycling.set(true); // continue in indypets$showState below

			if (cycleState((TameableEntity) (Object) this)) {
				return value;
			} else {
				return sitting;
			}
		}

		return value;
	}

	@Inject(method = "setSitting", at = @At("RETURN"))
	private void indypets$showState(boolean sitting, CallbackInfo ci, @Share("cycling") LocalBooleanRef cycling) {
		if (cycling.get()) {
			showPetStatus(IndyPets.interactingPlayer.get(), (TameableEntity) (Object) this, true);

			IndyPets.interactingPlayer.remove(); // for safety
		}
	}

	@Inject(method = "setOwnerUuid", at = @At(value = "TAIL"))
	protected void indypets$initFollowData(CallbackInfo ci) {
		if (world.isClient)
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
		indypets$isIndependent = !nbt.getBoolean("AllowedToFollow");

		if (nbt.contains("IndyPets$HomePos", 9)) {
			NbtList nbtList = nbt.getList("IndyPets$HomePos", 3);
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

	@Unique
	public void indypets$setHome() {
		indypets$homePos = getBlockPos();
	}
}

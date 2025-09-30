package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.util.Independence;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntity implements Independence {
	@Shadow public abstract boolean isTamed();

	@Unique boolean indypets$isIndependent = false;
	@Unique BlockPos indypets$homePos;

	protected TameableEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	public LazyEntityReference<LivingEntity> getOwnerReference() { throw new AssertionError(); }

	@Inject(
		method = {
			"setOwner(Lnet/minecraft/entity/LivingEntity;)V",
			"setOwner(Lnet/minecraft/entity/LazyEntityReference;)V"
		},
		at = @At(value = "HEAD")
	)
	protected void indypets$capturePreviousOwner(CallbackInfo ci, @Share("ownerRef") LocalRef<LazyEntityReference<LivingEntity>> owner) {
		if (getEntityWorld().isClient())
			return;

		owner.set(getOwnerReference());
	}

	@Inject(
		method = {
			"setOwner(Lnet/minecraft/entity/LivingEntity;)V",
			"setOwner(Lnet/minecraft/entity/LazyEntityReference;)V"
		},
		at = @At(value = "TAIL")
	)
	protected void indypets$initFollowData(CallbackInfo ci, @Share("ownerRef") LocalRef<LazyEntityReference<LivingEntity>> owner) {
		if (getEntityWorld().isClient())
			return;

		var oldRef = owner.get();
		var newRef = getOwnerReference();
		var oldUuid = oldRef != null ? oldRef.getUuid() : null;
		var newUuid = newRef != null ? newRef.getUuid() : null;

		if (!Objects.equals(oldUuid, newUuid)) {
			IndyPets.LOGGER.debug("{} changed owner from {} to {}", this, oldUuid, newUuid);
			indypets$isIndependent = false;
			indypets$setHome();
		}
	}

	@Inject(method = "writeCustomData", at = @At("HEAD"))
	private void indypets$writeFollowDataToNbt(WriteView view, CallbackInfo ci) {
		view.putBoolean("AllowedToFollow", !indypets$isIndependent);

		if (indypets$homePos != null) {
			view.put("IndyPets$HomePos", BlockPos.CODEC, indypets$homePos);
		}
	}

	@Inject(method = "readCustomData", at = @At("TAIL"))
	private void indypets$readFollowDataFromNbt(ReadView view, CallbackInfo ci) {
		indypets$isIndependent = !view.getBoolean("AllowedToFollow", true);

		var homePos = view.read("IndyPets$HomePos", BlockPos.CODEC);
		if (homePos.isPresent()) {
			indypets$homePos = homePos.get();
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

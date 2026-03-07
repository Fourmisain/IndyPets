package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;
import static com.lizin5ths.indypets.util.IndyPetsUtil.resetFollowData;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntity {
	protected TameableEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	public abstract LazyEntityReference<LivingEntity> getOwnerReference();

	@ModifyReturnValue(method = "shouldTryTeleportToOwner", at = @At(value = "RETURN"))
	protected boolean indypets$disableTeleporting(boolean original) {
		if (isActiveIndependent(this))
			return false;

		return original;
	}

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
			resetFollowData(this);
		}
	}
}

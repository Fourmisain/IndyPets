package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;
import static com.lizin5ths.indypets.util.IndyPetsUtil.resetFollowData;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin extends Animal {
	protected TamableAnimalMixin(EntityType<? extends Animal> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	public abstract EntityReference<LivingEntity> getOwnerReference();

	@ModifyReturnValue(method = "shouldTryTeleportToOwner", at = @At(value = "RETURN"))
	protected boolean indypets$disableTeleporting(boolean original) {
		if (isActiveIndependent(this))
			return false;

		return original;
	}

	@Inject(
		method = {
			"setOwner(Lnet/minecraft/world/entity/LivingEntity;)V",
			"setOwnerReference(Lnet/minecraft/world/entity/EntityReference;)V"
		},
		at = @At(value = "HEAD")
	)
	protected void indypets$capturePreviousOwner(CallbackInfo ci, @Share("ownerRef") LocalRef<EntityReference<LivingEntity>> owner) {
		if (level().isClientSide())
			return;

		owner.set(getOwnerReference());
	}

	@Inject(
		method = {
			"setOwner(Lnet/minecraft/world/entity/LivingEntity;)V",
			"setOwnerReference(Lnet/minecraft/world/entity/EntityReference;)V"
		},
		at = @At(value = "TAIL")
	)
	protected void indypets$initFollowData(CallbackInfo ci, @Share("ownerRef") LocalRef<EntityReference<LivingEntity>> owner) {
		if (level().isClientSide())
			return;

		var oldRef = owner.get();
		var newRef = getOwnerReference();
		var oldUuid = oldRef != null ? oldRef.getUUID() : null;
		var newUuid = newRef != null ? newRef.getUUID() : null;

		if (!Objects.equals(oldUuid, newUuid)) {
			IndyPets.LOGGER.debug("{} changed owner from {} to {}", this, oldUuid, newUuid);
			resetFollowData(this);
		}
	}
}

package com.lizin5ths.indypets.mixin.compat.suikefoxfriend;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import suike.suikefoxfriend.api.IFoxTamed;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;
import static com.lizin5ths.indypets.util.IndyPetsUtil.resetFollowData;

@SuppressWarnings("ConstantValue")
@Mixin(value = FoxEntity.class, priority = 1500)
public abstract class FoxEntityMixinMixin extends AnimalEntity implements IFoxTamed {
	protected FoxEntityMixinMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@TargetHandler(
		mixin = "suike.suikefoxfriend.mixin.FoxMixin",
		name = "playerTamedFox"
	)
	@Inject(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/FoxEntity;setPersistent()V"))
	public void indypets$initFollowData(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		resetFollowData(this);
	}

	@TargetHandler(
		mixin = "suike.suikefoxfriend.mixin.FoxMixin",
		name = "onTick"
	)
	@ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/FoxEntity;isWaiting()Z", ordinal = 0))
	public boolean indypets$dontSitOnLogoutWhenIndependent(boolean original) {
		return original || isActiveIndependent((FoxEntity) (Object) this);
	}

	@TargetHandler(
		mixin = "suike.suikefoxfriend.mixin.FoxMixin",
		name = "onTick"
	)
	@ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/FoxEntity;isWaiting()Z", ordinal = 1))
	public boolean indypets$doNotTeleport(boolean original) {
		return original && !isActiveIndependent((FoxEntity) (Object) this);
	}
}

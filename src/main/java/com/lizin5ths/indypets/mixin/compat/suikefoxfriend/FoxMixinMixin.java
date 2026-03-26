package com.lizin5ths.indypets.mixin.compat.suikefoxfriend;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import suike.suikefoxfriend.api.IFoxTamed;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;
import static com.lizin5ths.indypets.util.IndyPetsUtil.resetFollowData;

@SuppressWarnings("ConstantValue")
@Mixin(value = Fox.class, priority = 1500)
public abstract class FoxMixinMixin extends Animal implements IFoxTamed {
	protected FoxMixinMixin(EntityType<? extends Animal> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "registerGoals", at = @At("HEAD"))
	protected void registerGoals(CallbackInfo ci) {

	}

	@TargetHandler(
		mixin = "suike.suikefoxfriend.mixin.FoxMixin",
		name = "playerTamedFox"
	)
	@Inject(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/fox/Fox;setPersistenceRequired()V"))
	public void indypets$initFollowData(Player player, CallbackInfoReturnable<Boolean> cir) {
		resetFollowData(this);
	}

	@TargetHandler(
		mixin = "suike.suikefoxfriend.mixin.FoxMixin",
		name = "onTick"
	)
	@ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/fox/Fox;isWaiting()Z", ordinal = 0))
	public boolean indypets$dontSitOnLogoutWhenIndependent(boolean original) {
		return original || isActiveIndependent((Fox) (Object) this);
	}

	@TargetHandler(
		mixin = "suike.suikefoxfriend.mixin.FoxMixin",
		name = "onTick"
	)
	@ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/fox/Fox;isWaiting()Z", ordinal = 1))
	public boolean indypets$doNotTeleport(boolean original) {
		return original && !isActiveIndependent((Fox) (Object) this);
	}
}

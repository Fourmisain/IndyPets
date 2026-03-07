package com.lizin5ths.indypets.mixin.compat.suikefoxfriend;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

@Mixin(value = ServerEntityManager.class, priority = 1500)
public abstract class ServerEntityManagerMixinMixin {
	@TargetHandler(
		mixin = "suike.suikefoxfriend.mixin.ServerEntityManagerMixin",
		name = "onUnload"
	)
	@ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lsuike/suikefoxfriend/api/IFoxTamed;isWaiting()Z"))
	public boolean indypets$dontTeleportWhenIndependent(boolean original, @Local(argsOnly = true) EntityLike entity) {
		assert entity instanceof FoxEntity;
		return original || isActiveIndependent((FoxEntity) entity);
	}
}

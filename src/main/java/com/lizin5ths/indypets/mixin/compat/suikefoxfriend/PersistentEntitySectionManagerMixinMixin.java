package com.lizin5ths.indypets.mixin.compat.suikefoxfriend;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isActiveIndependent;

@Mixin(value = PersistentEntitySectionManager.class, priority = 1500)
public abstract class PersistentEntitySectionManagerMixinMixin {
	@TargetHandler(
		mixin = "suike.suikefoxfriend.mixin.PersistentEntitySectionManagerMixin",
		name = "onUnloadEntity"
	)
	@ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lsuike/suikefoxfriend/api/IFoxTamed;isWaiting()Z"))
	public boolean indypets$dontTeleportWhenIndependent(boolean original, @Local(argsOnly = true) EntityAccess entity) {
		assert entity instanceof Fox;
		return original || isActiveIndependent((Fox) entity);
	}
}

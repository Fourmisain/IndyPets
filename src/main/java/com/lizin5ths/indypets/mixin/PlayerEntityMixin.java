package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.util.IndyPetsUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@ModifyArg(method = "dropShoulderEntity", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
	public Consumer<Entity> indypets$setHome(Consumer<Entity> action) {
		return entity -> {
			if (entity instanceof TameableEntity) {
				IndyPetsUtil.setHome((TameableEntity) entity);
			}

			action.accept(entity);
		};
	}
}

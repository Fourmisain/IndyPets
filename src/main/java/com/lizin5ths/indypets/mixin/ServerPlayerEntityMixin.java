package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.IndyPetsUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
	public ServerPlayerEntityMixin(World world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "onDisconnect", at = @At("TAIL"))
	public void indypets$forgetPlayerData(CallbackInfo ci) {
		ServerConfig.HAS_MOD_INSTALLED.remove(getUuid());
		ServerConfig.RECEIVED_PLAYER_CONFIGS.remove(getUuid());
	}

	@ModifyArg(method = "spawnShoulderEntity", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
	public Consumer<Entity> indypets$setHome(Consumer<Entity> action) {
		return entity -> {
			if (entity instanceof TameableEntity tameable) {
				IndyPetsUtil.setHome(tameable);
			}

			action.accept(entity);
		};
	}
}

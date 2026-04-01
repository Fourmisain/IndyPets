package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.config.ServerConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
	public ServerPlayerMixin(Level level, GameProfile profile) {
		super(level, profile);
	}

	@Inject(method = "disconnect", at = @At("TAIL"))
	public void indypets$forgetPlayerData(CallbackInfo ci) {
		ServerConfig.HAS_MOD_INSTALLED.remove(getUUID());
		ServerConfig.RECEIVED_PLAYER_CONFIGS.remove(getUUID());
	}

	@ModifyArg(method = "respawnEntityOnShoulder", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
	public Consumer<Entity> indypets$setHome(Consumer<Entity> action) {
		return entity -> {
			if (isSupported(entity) && isTamed(entity)) {
				setHome(entity);
			}

			action.accept(entity);
		};
	}
}

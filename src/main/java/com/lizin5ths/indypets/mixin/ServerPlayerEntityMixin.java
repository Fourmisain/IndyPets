package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.config.ServerConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Forget player data when player leaves the server */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "onDisconnect", at = @At("TAIL"))
	public void forgetIndyPetsPlayerData(CallbackInfo ci) {
		ServerConfig.HAS_MOD_INSTALLED.remove(getUuid());
		ServerConfig.PLAYER_CONFIG.remove(getUuid());
	}
}

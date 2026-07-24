package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.WhistleState;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements WhistleState {
	@Unique
	boolean indypets$hornState = true;

	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Override
	public boolean indypets$setHornState() {
		return indypets$hornState;
	}

	@Override
	public void indypets$setHornState(boolean hornState) {
		indypets$hornState = hornState;
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void indypets$readWhistleState(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.contains("indypets$horn_state", NbtElement.BYTE_TYPE)) {
			indypets$hornState = nbt.getBoolean("indypets$horn_state");
		} else {
			indypets$hornState = true;
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void indypets$writeWhistleState(NbtCompound nbt, CallbackInfo ci) {
		nbt.putBoolean("indypets$horn_state", indypets$hornState);
	}

	@Inject(method = "copyFrom", at = @At("TAIL"))
	public void indypets$persistWhistleState(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		indypets$hornState = ((WhistleState) oldPlayer).indypets$setHornState();
	}

	@Inject(method = "onDisconnect", at = @At("TAIL"))
	public void indypets$forgetPlayerData(CallbackInfo ci) {
		ServerConfig.HAS_MOD_INSTALLED.remove(getUuid());
		ServerConfig.RECEIVED_PLAYER_CONFIGS.remove(getUuid());
	}
}

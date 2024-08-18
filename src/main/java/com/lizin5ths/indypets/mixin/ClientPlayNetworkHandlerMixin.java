package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.network.Networking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Send player data to the server */
@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
	@Inject(method = "onGameJoin", at = @At("TAIL"))
	public void indypets$sendPlayerData(GameJoinS2CPacket packet, CallbackInfo ci) {
		Networking.sendClientConfig();
	}
}

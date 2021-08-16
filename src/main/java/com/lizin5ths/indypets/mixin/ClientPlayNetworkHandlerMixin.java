package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.network.Networking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Inform the server that we have the mod installed */
@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
	@Inject(method = "onGameJoin", at = @At("TAIL"))
	public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		ClientPlayNetworking.send(Networking.MOD_INSTALLED, PacketByteBufs.empty());
		Networking.sendClientConfig();
	}
}

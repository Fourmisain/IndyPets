package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
	@Inject(method = "onPlayerInteractEntity", at = @At("RETURN"))
	public void indypets$disableInteractHook(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
		IndyPets.interactingPlayer.remove(); // for safety
	}
}

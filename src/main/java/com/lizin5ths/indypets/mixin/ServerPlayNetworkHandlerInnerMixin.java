package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.IndyPets;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/network/ServerPlayNetworkHandler$1")
public abstract class ServerPlayNetworkHandlerInnerMixin {
	@Inject(method = "interact", at = @At("RETURN"))
	public void indypets$disableInteractHook(Hand hand, CallbackInfo ci) {
		IndyPets.interactingPlayer.remove(); // for safety
	}
}

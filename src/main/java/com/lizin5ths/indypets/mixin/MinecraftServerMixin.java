package com.lizin5ths.indypets.mixin;

import com.lizin5ths.indypets.config.Config;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Inject(method = "shutdown", at = @At("HEAD"))
	public void indypets$saveConfig(CallbackInfo ci) {
		Config.save();
	}
}

package com.lizin5ths.indypets.mixin.access;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerConfigurationNetworkHandler.class)
public interface ServerConfigurationNetworkHandlerAccessor {
	@Accessor("profile")
	GameProfile getGameProfile();
}

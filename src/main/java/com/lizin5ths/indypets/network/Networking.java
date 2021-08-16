package com.lizin5ths.indypets.network;

import com.lizin5ths.indypets.IndyPets;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class Networking {
	public static final Identifier MOD_INSTALLED = new Identifier(IndyPets.MOD_ID, "mod_installed");

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(MOD_INSTALLED, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				IndyPets.hasModInstalled.add(player.getUuid());
			});
		});
	}
}

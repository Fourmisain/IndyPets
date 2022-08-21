package com.lizin5ths.indypets.network;

import com.google.gson.JsonSyntaxException;
import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class Networking {
	public static final Identifier MOD_INSTALLED = IndyPets.id("mod_installed");
	public static final Identifier PLAYER_CONFIG = IndyPets.id("config");

	public static void sendModInstalled() throws IllegalStateException {
		ClientPlayNetworking.send(Networking.MOD_INSTALLED, PacketByteBufs.empty());
	}

	public static void sendClientConfig() throws IllegalStateException {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeString(Config.GSON.toJson(Config.LOCAL_CONFIG));
		ClientPlayNetworking.send(PLAYER_CONFIG, buf);
	}

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(Networking.MOD_INSTALLED, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				ServerConfig.HAS_MOD_INSTALLED.add(player.getUuid());
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(Networking.PLAYER_CONFIG, (server, player, handler, buf, responseSender) -> {
			String json = buf.readString(32767);

			server.execute(() -> {
				try {
					Config config = Config.GSON.fromJson(json, Config.class);
					ServerConfig.PLAYER_CONFIG.put(player.getUuid(), config);
				} catch (JsonSyntaxException e) {
					IndyPets.LOGGER.error("couldn't parse received player config! \"{}\"", json, e);
				}
			});
		});
	}
}

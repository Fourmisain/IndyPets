package com.lizin5ths.indypets.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
	public static final Identifier MOD_INSTALLED = new Identifier(IndyPets.MOD_ID, "mod_installed");
	public static final Identifier PLAYER_CONFIG = new Identifier(IndyPets.MOD_ID, "config");

	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	public static void sendClientConfig() throws IllegalStateException {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeString(GSON.toJson(Config.CONFIG));
		ClientPlayNetworking.send(PLAYER_CONFIG, buf);
	}

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(Networking.MOD_INSTALLED, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				ServerConfig.hasModInstalled.add(player.getUuid());
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(Networking.PLAYER_CONFIG, (server, player, handler, buf, responseSender) -> {
			String json = buf.readString(32767);

			server.execute(() -> {
				try {
					Config config = GSON.fromJson(json, Config.class);
					ServerConfig.PLAYER_CONFIG.put(player.getUuid(), config);
				} catch (JsonSyntaxException e) {
					IndyPets.LOGGER.error("couldn't parse received player config!");
				}
			});
		});
	}
}

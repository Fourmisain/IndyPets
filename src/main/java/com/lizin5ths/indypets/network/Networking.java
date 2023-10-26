package com.lizin5ths.indypets.network;

import com.google.gson.JsonSyntaxException;
import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.IndyPetsUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class Networking {
	public static final Identifier MOD_INSTALLED = IndyPets.id("mod_installed");
	public static final Identifier PLAYER_CONFIG = IndyPets.id("config");
	public static final Identifier PET_INTERACT  = IndyPets.id("pet_interact");

	public static void sendModInstalled() throws IllegalStateException {
		ClientPlayNetworking.send(Networking.MOD_INSTALLED, PacketByteBufs.empty());
	}

	public static void sendClientConfig() throws IllegalStateException {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeString(Config.GSON.toJson(Config.LOCAL_CONFIG));
		ClientPlayNetworking.send(PLAYER_CONFIG, buf);
	}

	public static void sendPetInteract(TameableEntity entity) throws IllegalStateException {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(entity.getId());
		ClientPlayNetworking.send(PET_INTERACT, buf);
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

		ServerPlayNetworking.registerGlobalReceiver(Networking.PET_INTERACT, (server, player, handler, buf, responseSender) -> {
			int entityId = buf.readVarInt();

			server.execute(() -> {
				Entity entity = player.getWorld().getEntityById(entityId);
				if (entity instanceof TameableEntity) {
					IndyPetsUtil.changeFollowing(player, (TameableEntity) entity);
				}
			});
		});
	}
}

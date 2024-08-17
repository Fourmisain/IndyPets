package com.lizin5ths.indypets.network;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.util.IndyPetsUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class Networking {
	public static void sendClientConfig() throws IllegalStateException {
		ClientPlayNetworking.send(new PlayerConfigPayload(Config.LOCAL_CONFIG));
	}

	public static void sendPetInteract(TameableEntity entity) throws IllegalStateException {
		ClientPlayNetworking.send(new PetInteractPayload(entity.getId()));
	}

	public static void init() {
		PayloadTypeRegistry.playC2S().register(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PlayerConfigPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			MinecraftServer server = player.getServer();
			if (server != null) {
				server.execute(() -> {
					ServerConfig.HAS_MOD_INSTALLED.add(player.getUuid());
					if (payload != null)
						ServerConfig.PLAYER_CONFIG.put(player.getUuid(), payload.config());
				});
			}
		});

		PayloadTypeRegistry.playC2S().register(PetInteractPayload.ID, PetInteractPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PetInteractPayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			MinecraftServer server = player.getServer();

			if (server != null) {
				server.execute(() -> {
					Entity entity = player.getWorld().getEntityById(payload.entityId());
					if (entity instanceof TameableEntity) {
						if (IndyPetsUtil.changeFollowing(player, (TameableEntity) entity)) {
							IndyPetsUtil.showPetStatus(player, (TameableEntity) entity, true);
						}
					}
				});
			}
		});
	}
}

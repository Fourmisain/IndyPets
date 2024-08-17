package com.lizin5ths.indypets.network;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.mixin.access.ServerConfigurationNetworkHandlerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;

public class Networking {
	public static void sendClientConfigConfPhase() throws IllegalStateException {
		ClientConfigurationNetworking.send(new PlayerConfigPayload(Config.local()));
	}

	public static void sendClientConfig() throws IllegalStateException {
		ClientPlayNetworking.send(new PlayerConfigPayload(Config.local()));
	}

	public static void sendPetInteract(TameableEntity entity) throws IllegalStateException {
		ClientPlayNetworking.send(new PetInteractPayload(entity.getId()));
	}

	public static void init() {
		PayloadTypeRegistry.configurationC2S().register(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC);
		ServerConfigurationNetworking.registerGlobalReceiver(PlayerConfigPayload.ID, (payload, context) -> {
			UUID playerUuid = ((ServerConfigurationNetworkHandlerAccessor) context.networkHandler()).getGameProfile().getId();
			MinecraftServer server = context.server();

			if (server != null) {
				server.execute(() -> {
					ServerConfig.HAS_MOD_INSTALLED.add(playerUuid);
					if (payload != null)
						ServerConfig.RECEIVED_PLAYER_CONFIGS.put(playerUuid, payload.config());
				});
			}
		});

		PayloadTypeRegistry.playC2S().register(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PlayerConfigPayload.ID, (payload, context) -> {
			UUID playerUuid = context.player().getUuid();
			MinecraftServer server = context.server();

			if (server != null) {
				server.execute(() -> {
					if (payload != null)
						ServerConfig.RECEIVED_PLAYER_CONFIGS.put(playerUuid, payload.config());
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

					if (canInteract(player, entity)) {
						TameableEntity tameable = (TameableEntity) entity;
						toggleIndependence(tameable);
						showPetStatus(player, tameable, true);
					}
				});
			}
		});
	}

	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		ClientConfigurationConnectionEvents.START.register((handler, client) -> {
			sendClientConfigConfPhase();
		});
	}
}

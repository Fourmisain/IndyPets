package com.lizin5ths.indypets.network;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import com.lizin5ths.indypets.mixin.access.ServerConfigurationPacketListenerImplAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import java.util.UUID;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;

public class Networking {
	public static void sendClientConfigConfPhase() throws IllegalStateException {
		if (ClientConfigurationNetworking.canSend(PlayerConfigPayload.TYPE)) {
			ClientConfigurationNetworking.send(new PlayerConfigPayload(Config.local()));
		}
	}

	public static void sendClientConfig() throws IllegalStateException {
		if (ClientPlayNetworking.canSend(PlayerConfigPayload.TYPE)) {
			ClientPlayNetworking.send(new PlayerConfigPayload(Config.local()));
		}
	}

	public static void sendPetInteract(Entity entity) throws IllegalStateException {
		ClientPlayNetworking.send(new PetInteractPayload(entity.getId()));
	}

	@SuppressWarnings({"resource", "DataFlowIssue"})
	public static void init() {
		PayloadTypeRegistry.serverboundConfiguration().register(PlayerConfigPayload.TYPE, PlayerConfigPayload.CODEC);
		ServerConfigurationNetworking.registerGlobalReceiver(PlayerConfigPayload.TYPE, (payload, context) -> {
			UUID playerUuid = ((ServerConfigurationPacketListenerImplAccessor) context.packetListener()).getGameProfile().id();

			context.server().execute(() -> {
				ServerConfig.HAS_MOD_INSTALLED.add(playerUuid);
				ServerConfig.RECEIVED_PLAYER_CONFIGS.put(playerUuid, payload.config());
			});
		});

		PayloadTypeRegistry.serverboundPlay().register(PlayerConfigPayload.TYPE, PlayerConfigPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PlayerConfigPayload.TYPE, (payload, context) -> {
			UUID playerUuid = context.player().getUUID();

			context.server().execute(() -> {
				ServerConfig.RECEIVED_PLAYER_CONFIGS.put(playerUuid, payload.config());
			});
		});

		PayloadTypeRegistry.serverboundPlay().register(PetInteractPayload.TYPE, PetInteractPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PetInteractPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();

			context.server().execute(() -> {
				Entity entity = player.level().getEntity(payload.entityId());

				if (canInteract(player, entity)) {
					toggleIndependence(entity);
					showPetStatus(player, entity, true);
				}
			});
		});
	}

	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		ClientConfigurationConnectionEvents.START.register((_, _) -> {
			sendClientConfigConfPhase();
		});
	}
}

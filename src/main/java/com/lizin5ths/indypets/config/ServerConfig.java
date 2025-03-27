package com.lizin5ths.indypets.config;

import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerConfig {
	public static final Set<UUID> HAS_MOD_INSTALLED = new HashSet<>();
	public static final Map<UUID, Config> RECEIVED_PLAYER_CONFIGS = new HashMap<>();

	public static Config getDefaultedPlayerConfig(@Nullable UUID playerUuid) {
		// use client-sent config if possible
		Config config = RECEIVED_PLAYER_CONFIGS.get(playerUuid);
		if (config != null) return config;

		// use server-stored config if possible
		config = Config.local().vanillaPlayerConfigs.get(playerUuid);
		if (config != null) return config;

		// default to server config
		return Config.local();
	}

	public static Config getDefaultedPlayerConfig(LazyEntityReference<LivingEntity> player) {
		if (player == null){
			return getDefaultedPlayerConfig((UUID) null);
		}
		return getDefaultedPlayerConfig(player.getUuid());
	}
}

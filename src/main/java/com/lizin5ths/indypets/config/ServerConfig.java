package com.lizin5ths.indypets.config;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerConfig {
	public static final Set<UUID> HAS_MOD_INSTALLED = new HashSet<>();
	public static final Map<UUID, Config> PLAYER_CONFIG = new HashMap<>();

	public static Config getDefaultedPlayerConfig(@Nullable UUID playerUuid) {
		Config config = PLAYER_CONFIG.get(playerUuid);
		if (config != null) return config;
		return Config.LOCAL_CONFIG;
	}
}

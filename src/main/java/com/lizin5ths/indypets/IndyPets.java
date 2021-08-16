package com.lizin5ths.indypets;

import com.google.gson.GsonBuilder;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.network.Networking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import java.util.HashSet;
import java.util.UUID;

public class IndyPets implements ModInitializer {
	public static final String MOD_ID = "indypets";

	public static final HashSet<UUID> hasModInstalled = new HashSet<>();

	@Override
	public void onInitialize() {
		Config.CONFIG = AutoConfig.register(Config.class, (definition, configClass) -> new GsonConfigSerializer<>(
			definition, configClass, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create())
		).getConfig();

		Networking.init();
	}
}
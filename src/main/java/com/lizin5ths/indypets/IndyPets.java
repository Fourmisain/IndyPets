package com.lizin5ths.indypets;

import com.google.gson.GsonBuilder;
import com.lizin5ths.indypets.config.IndyPetsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.UUID;

public class IndyPets implements ModInitializer {
	public static final String MOD_ID = "indypets";

	public static final Identifier MOD_INSTALLED = new Identifier(MOD_ID, "mod_installed");
	public static final HashSet<UUID> hasModInstalled = new HashSet<>();

	@Override
	public void onInitialize() {
		IndyPetsConfig.CONFIG = AutoConfig.register(IndyPetsConfig.class, (definition, configClass) -> new GsonConfigSerializer<>(
			definition, configClass, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create())
		).getConfig();

		ServerPlayNetworking.registerGlobalReceiver(MOD_INSTALLED, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				hasModInstalled.add(player.getUuid());
			});
		});
	}
}
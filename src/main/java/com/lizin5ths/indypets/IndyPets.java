package com.lizin5ths.indypets;

import com.lizin5ths.indypets.command.Commands;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.network.Networking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IndyPets implements ModInitializer {
	public static final String MOD_ID = "indypets";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static ThreadLocal<PlayerEntity> interactingPlayer = ThreadLocal.withInitial(() -> null);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		Config.init();
		Networking.init();
		Commands.init();
	}
}

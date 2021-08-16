package com.lizin5ths.indypets;

import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.network.Networking;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IndyPets implements ModInitializer {
	public static final String MOD_ID = "indypets";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Config.init();
		Networking.init();
	}
}
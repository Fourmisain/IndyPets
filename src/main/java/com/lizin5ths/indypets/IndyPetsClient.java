package com.lizin5ths.indypets;

import com.lizin5ths.indypets.config.Config;
import net.fabricmc.api.ClientModInitializer;

public class IndyPetsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Config.clientInit();
	}
}

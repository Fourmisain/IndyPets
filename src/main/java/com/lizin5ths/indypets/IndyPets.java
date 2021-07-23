package com.lizin5ths.indypets;

import net.fabricmc.api.ModInitializer;

public class IndyPets implements ModInitializer {
	@Override
	public void onInitialize() {
		IndyPetsConfig.load();
	}
}
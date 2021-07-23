package com.lizin5ths.indypets;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class IndyPetsMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfig.getConfigScreen(IndyPetsConfig.class, parent).get();
	}
}
package com.lizin5ths.indypets;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = IndyPets.MOD_ID)
public class IndyPetsConfig implements ConfigData {

	@ConfigEntry.Gui.Tooltip(count = 2)
	public boolean selectiveFollowing = true;
	@ConfigEntry.Gui.Tooltip
	public boolean disableCatFollow = true;
	@ConfigEntry.Gui.Tooltip
	public boolean disableParrotFollow = true;
	@ConfigEntry.Gui.Tooltip
	public boolean disableWolfFollow = true;

	@ConfigEntry.Gui.Tooltip
	public boolean silentMode = false;
}

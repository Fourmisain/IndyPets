package com.lizin5ths.indypets;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

//@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
@Config(name = IndyPets.MOD_ID)
public class IndyPetsConfig implements ConfigData {
	public boolean selectiveFollowing = true;
	public boolean silentMode = false;

	public boolean disableCatFollow = true;
	public boolean disableParrotFollow = true;
	public boolean disableWolfFollow = true;
}

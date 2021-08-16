package com.lizin5ths.indypets.config;

import com.lizin5ths.indypets.IndyPets;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;

@Config(name = IndyPets.MOD_ID)
public class IndyPetsConfig implements ConfigData {
	public static transient IndyPetsConfig CONFIG;

	@ConfigEntry.Gui.Tooltip(count = 2)
	public boolean selectiveFollowing = true;
	public boolean independentCats = true;
	public boolean independentParrots = true;
	public boolean independentWolves = true;

	@ConfigEntry.Gui.Tooltip
	public boolean silentMode = false;

	public boolean getDefaultIndependence(TameableEntity tameable) {
		if (tameable instanceof CatEntity)    return independentCats;
		if (tameable instanceof ParrotEntity) return independentParrots;
		if (tameable instanceof WolfEntity)   return independentWolves;
		return true;
	}
}

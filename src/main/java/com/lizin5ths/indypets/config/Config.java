package com.lizin5ths.indypets.config;

import com.google.gson.GsonBuilder;
import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.network.Networking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ActionResult;

@SuppressWarnings("CanBeFinal")
@me.shedaniel.autoconfig.annotation.Config(name = IndyPets.MOD_ID)
public class Config implements ConfigData {
	@ConfigEntry.Gui.Excluded
	public static transient Config LOCAL_CONFIG;

	public boolean independentCats = true;
	public boolean independentParrots = true;
	public boolean independentWolves = true;

	public boolean silentMode = false;

	public boolean getDefaultIndependence(TameableEntity tameable) {
		if (tameable instanceof CatEntity)    return independentCats;
		if (tameable instanceof ParrotEntity) return independentParrots;
		if (tameable instanceof WolfEntity)   return independentWolves;
		return true;
	}

	public static void init() {
		ConfigHolder<Config> configHolder = AutoConfig.register(Config.class, (definition, configClass) -> new GsonConfigSerializer<>(
			definition, configClass, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create())
		);

		configHolder.registerSaveListener((manager, config) -> {
			try {
				Networking.sendClientConfig();
			} catch (IllegalStateException ignored) {}
			return ActionResult.PASS;
		});

		LOCAL_CONFIG = configHolder.get();
	}
}
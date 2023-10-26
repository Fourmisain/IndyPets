package com.lizin5ths.indypets.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.network.Networking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

@SuppressWarnings("CanBeFinal")
@me.shedaniel.autoconfig.annotation.Config(name = IndyPets.MOD_ID)
public class Config implements ConfigData {
	public static GsonBuilder getGsonBuilder() {
		return new GsonBuilder()
			.registerTypeAdapter(Blocklist.class, BlocklistTypeAdapter.INST)
			.registerTypeAdapter(Identifier.class, IdentifierTypeAdapter.INST)
			.disableHtmlEscaping();
	}

	@ConfigEntry.Gui.Excluded
	public static final Gson GSON = getGsonBuilder().create();
	@ConfigEntry.Gui.Excluded
	public static final Gson GSON_PRETTY = getGsonBuilder().setPrettyPrinting().create();

	@ConfigEntry.Gui.Excluded
	public static Config LOCAL_CONFIG;

	public boolean independentCats = true;
	public boolean independentParrots = true;
	public boolean independentWolves = true;

	public boolean silentMode = false;

	@ConfigEntry.Gui.Tooltip
	public Blocklist blocklist = Blocklist.getDefault();

	@ConfigEntry.Gui.Tooltip
	public Blocklist interactBlocklist = Blocklist.getInteractDefault();

	@ConfigEntry.Gui.Tooltip
	public Identifier interactItem = null;

	public boolean getDefaultIndependence(TameableEntity tameable) {
		if (tameable instanceof CatEntity)    return independentCats;
		if (tameable instanceof ParrotEntity) return independentParrots;
		if (tameable instanceof WolfEntity)   return independentWolves;
		return true;
	}

	public static void init() {
		ConfigHolder<Config> configHolder = AutoConfig.register(Config.class, (definition, configClass) -> new GsonConfigSerializer<>(definition, configClass, GSON_PRETTY));

		configHolder.registerSaveListener((manager, config) -> {
			try {
				Networking.sendClientConfig();
			} catch (IllegalStateException ignored) {}
			return ActionResult.PASS;
		});

		LOCAL_CONFIG = configHolder.get();
	}

	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		AutoConfig.getGuiRegistry(Config.class).registerPredicateProvider(new BlocklistGuiProvider(), field -> field.getType().equals(Blocklist.class));
		AutoConfig.getGuiRegistry(Config.class).registerPredicateProvider(new ItemGuiProvider(), field -> field.getName().equals("interactItem"));
	}
}

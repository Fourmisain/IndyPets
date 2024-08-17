package com.lizin5ths.indypets.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.network.Networking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("CanBeFinal")
@me.shedaniel.autoconfig.annotation.Config(name = IndyPets.MOD_ID)
public class Config implements ConfigData {
	public static GsonBuilder getGsonBuilder() {
		return new GsonBuilder()
			.registerTypeAdapter(Blocklist.class, BlocklistTypeAdapter.INST)
			.registerTypeAdapter(Identifier.class, IdentifierTypeAdapter.INST)
			.registerTypeAdapterFactory(new VanillaPlayerConfigsTypeAdapter())
			.disableHtmlEscaping();
	}

	@ConfigEntry.Gui.Excluded
	public static final Gson GSON_EXCLUDE_LOCAL = getGsonBuilder().addSerializationExclusionStrategy(new LocalOnlyExclusionStrategy()).create();
	@ConfigEntry.Gui.Excluded
	public static final Gson GSON_PRETTY = getGsonBuilder().setPrettyPrinting().create();

	@ConfigEntry.Gui.Excluded
	private static ConfigHolder<Config> LOCAL_CONFIG;

	public static Config local() {
		return LOCAL_CONFIG.get();
	}

	public static Config vanillaCopyOf(Config other) {
		Config config = new Config();
		// see VanillaPlayerConfigsTypeAdapter
		config.regularInteract = other.regularInteract;
		config.sneakInteract = other.sneakInteract;
		config.silentMode = other.silentMode;
		config.homeRadius = other.homeRadius;
		return config;
	}

	public static Config vanilla(UUID playerUuid) {
		return local().vanillaPlayerConfigs.computeIfAbsent(playerUuid, k -> vanillaCopyOf(local()));
	}

	public static void resetVanilla(UUID playerUuid) {
		local().vanillaPlayerConfigs.remove(playerUuid);
	}

	// actual config

	public boolean independentCats = true;
	public boolean independentParrots = true;
	public boolean independentWolves = true;
	public boolean independentModdedPets = true;

	@ConfigEntry.Gui.Tooltip
	public boolean regularInteract = true;
	@ConfigEntry.Gui.Tooltip
	public boolean sneakInteract = true;

	public boolean silentMode = false;

	@ConfigEntry.Gui.Excluded
	public static final float INNER_HOME_PERCENTAGE_DEFAULT = 0.66f;

	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.BoundedDiscrete(max = 128)
	public int homeRadius = 80;
	public float innerHomePercentage = INNER_HOME_PERCENTAGE_DEFAULT;

	@ConfigEntry.Gui.Tooltip
	public Blocklist blocklist = Blocklist.getDefault();

	@ConfigEntry.Gui.Tooltip
	public Blocklist interactBlocklist = Blocklist.getInteractDefault();

	@ConfigEntry.Gui.Tooltip
	public Identifier interactItem = null;

	// client-only

	@LocalOnly
	@ConfigEntry.Gui.Tooltip
	public boolean positionedWhistleSound = false;

	// server-only

	@LocalOnly
	@ConfigEntry.Gui.Excluded
	public Map<UUID, Config> vanillaPlayerConfigs = new HashMap<>();

	public boolean getDefaultIndependence(TameableEntity tameable) {
		if (tameable instanceof CatEntity)    return independentCats;
		if (tameable instanceof ParrotEntity) return independentParrots;
		if (tameable instanceof WolfEntity)   return independentWolves;
		return independentModdedPets;
	}

	public static void init() {
		LOCAL_CONFIG = AutoConfig.register(Config.class, (definition, configClass) -> new GsonConfigSerializer<>(definition, configClass, GSON_PRETTY));
	}

	public static void save() {
		LOCAL_CONFIG.save();
	}

	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		GuiRegistry guiRegistry = AutoConfig.getGuiRegistry(Config.class);
		guiRegistry.registerPredicateProvider(new BlocklistGuiProvider(), field -> field.getType().equals(Blocklist.class));
		guiRegistry.registerPredicateProvider(new ItemGuiProvider(), field -> field.getName().equals("interactItem"));
		guiRegistry.registerPredicateProvider(new InnerHomeGuiProvider(), field -> field.getName().equals("innerHomePercentage"));

		LOCAL_CONFIG.registerSaveListener((manager, config) -> {
			if (!MinecraftClient.getInstance().isInSingleplayer()) {
				try {
					Networking.sendClientConfig();
				} catch (IllegalStateException ignored) {
				}
			}
			return ActionResult.PASS;
		});
	}
}

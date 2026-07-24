package com.lizin5ths.indypets.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.client.BlocklistGuiProvider;
import com.lizin5ths.indypets.config.client.HornConfigGuiProvider;
import com.lizin5ths.indypets.config.client.InnerHomeGuiProvider;
import com.lizin5ths.indypets.config.client.ItemGuiProvider;
import com.lizin5ths.indypets.network.Networking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

	private static Config vanillaCopyOf(Config original) {
		Config config = new Config();
		// vanilla configs currently only supports these values. needs to be in sync with VanillaPlayerConfigsTypeAdapter
		config.regularInteract = original.regularInteract;
		config.sneakInteract = original.sneakInteract;
		config.silentMode = original.silentMode;
		config.homeRadius = original.homeRadius;
		config.whistleRadius = original.whistleRadius;
		config.hornConfig = new HashMap<>(original.hornConfig);
		// the rest is referenced from the original config
		copyUnusedFromOriginal(config, original);
		return config;
	}

	private static void copyUnusedFromOriginal(Config config, Config original) {
		config.innerHomePercentage =  original.innerHomePercentage;
		config.blocklist = original.blocklist;
		config.interactBlocklist = original.interactBlocklist;
		config.interactItem = original.interactItem;
	}

	public static Config vanilla(UUID playerUuid) {
		return local().vanillaPlayerConfigs.computeIfAbsent(playerUuid, k -> vanillaCopyOf(local()));
	}

	public static void resetVanilla(UUID playerUuid) {
		local().vanillaPlayerConfigs.remove(playerUuid);
	}

	// actual config

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

	@ConfigEntry.BoundedDiscrete(min = 4, max = 144)
	public int whistleRadius = 96;

	@ConfigEntry.Gui.Tooltip
	public Blocklist blocklist = Blocklist.getDefault();

	@ConfigEntry.Gui.Tooltip
	public Blocklist interactBlocklist = Blocklist.getInteractDefault();

	@ConfigEntry.Gui.Tooltip
	public Identifier interactItem = null;

	@ConfigEntry.Category("horns")
	public Map<Identifier, HornSetting> hornConfig = new HashMap<>();

	// client-only

	@LocalOnly
	@ConfigEntry.Gui.Tooltip
	public boolean positionedWhistleSound = false;

	// server-only

	@LocalOnly
	@ConfigEntry.Gui.Excluded
	public Map<UUID, Config> vanillaPlayerConfigs = new HashMap<>();

	public HornSetting getHornSetting(Identifier hornId) {
		return hornConfig.getOrDefault(hornId, HornSetting.DISABLED);
	}

	public void setHornSetting(Identifier hornId, HornSetting hornSetting) {
		if (hornSetting == HornSetting.DISABLED) {
			hornConfig.remove(hornId);
		} else {
			hornConfig.put(hornId, hornSetting);
		}
	}

	public static void init() {
		LOCAL_CONFIG = AutoConfig.register(Config.class, (definition, configClass) -> new GsonConfigSerializer<>(definition, configClass, GSON_PRETTY));

		for (var config : local().vanillaPlayerConfigs.values()) {
			copyUnusedFromOriginal(config, local());
		}
	}

	public static void save() {
		LOCAL_CONFIG.save();
	}

	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		var guiRegistry = AutoConfig.getGuiRegistry(Config.class);
		guiRegistry.registerPredicateProvider(new BlocklistGuiProvider(), field -> field.getType().equals(Blocklist.class));
		guiRegistry.registerPredicateProvider(new ItemGuiProvider(), field -> field.getName().equals("interactItem"));
		guiRegistry.registerPredicateProvider(new InnerHomeGuiProvider(), field -> field.getName().equals("innerHomePercentage"));
		guiRegistry.registerPredicateProvider(new HornConfigGuiProvider(), field -> field.getName().equals("hornConfig"));

		LOCAL_CONFIG.registerSaveListener((manager, config) -> {
			try {
				Networking.sendClientConfig();
			} catch (IllegalStateException ignored) { }
			return ActionResult.PASS;
		});
	}
}

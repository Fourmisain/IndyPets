package com.lizin5ths.indypets.config.client;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.HornSetting;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HornConfigGuiProvider implements GuiProvider {
	private static final String EXPLANATION = "text.autoconfig.indypets.option.goatHornExplanation";
	private static final String NOT_IN_WORLD = "text.autoconfig.indypets.option.goatHornExplanation.notInWorld";

	@SuppressWarnings({"rawtypes"})
	@Override
	public List<AbstractConfigListEntry> get(String i13n, Field field, Object _config, Object defaults, GuiRegistryAccess registry) {
		try {
			Config config = (Config) _config;
			List<AbstractConfigListEntry> entries = new ArrayList<>();

			entries.add(
				ConfigEntryBuilder.create()
					.startTextDescription(Text.translatable(EXPLANATION))
					.setTooltip(Text.translatable(EXPLANATION + ".@Tooltip"))
					.build()
			);

			ClientWorld world = MinecraftClient.getInstance().world;
			if (world != null) {
				world.getRegistryManager().getOrThrow(RegistryKeys.INSTRUMENT)
					.getOptional(InstrumentTags.GOAT_HORNS)
					.ifPresent(registryEntries -> registryEntries.stream()
					.map(entry -> entry.getKey().map(RegistryKey::getValue).orElseThrow()) //
					.sorted()
					.forEachOrdered(hornId -> {
						Text name = Text.translatable(Util.createTranslationKey("instrument", hornId));

						entries.add(
							ConfigEntryBuilder.create()
								.startEnumSelector(name, HornSetting.class, config.getHornSetting(hornId))
								.setDefaultValue(HornSetting.DISABLED)
								.setSaveConsumer(setting -> config.setHornSetting(hornId, setting))
								.setEnumNameProvider(anEnum -> Text.translatable("text.autoconfig.indypets.option.goatHorn." + anEnum.name()))
								.build());
					}));
			} else {
				entries.add(
					ConfigEntryBuilder.create()
						.startTextDescription(Text.translatable(NOT_IN_WORLD))
						.setTooltip(Text.translatable(NOT_IN_WORLD + ".@Tooltip"))
						.build());
			}

			return entries;
		} catch (ClassCastException e) {
			IndyPets.LOGGER.error(e);
			return Collections.emptyList();
		}
	}
}
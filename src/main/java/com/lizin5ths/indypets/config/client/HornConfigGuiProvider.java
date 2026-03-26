package com.lizin5ths.indypets.config.client;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.HornSetting;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.InstrumentTags;
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
			var config = (Config) _config;
			List<AbstractConfigListEntry> entries = new ArrayList<>();

			entries.add(
				ConfigEntryBuilder.create()
					.startTextDescription(Component.translatable(EXPLANATION))
					.setTooltip(Component.translatable(EXPLANATION + ".@Tooltip"))
					.build()
			);

			ClientLevel world = Minecraft.getInstance().level;
			if (world != null) {
				world.registryAccess().lookupOrThrow(Registries.INSTRUMENT)
					.get(InstrumentTags.GOAT_HORNS)
					.ifPresent(registryEntries -> registryEntries.stream()
					.map(entry -> entry.unwrapKey().map(ResourceKey::identifier).orElseThrow()) //
					.sorted()
					.forEachOrdered(hornId -> {
						Component name = Component.translatable(Util.makeDescriptionId("instrument", hornId));

						entries.add(
							ConfigEntryBuilder.create()
								.startEnumSelector(name, HornSetting.class, config.getHornSetting(hornId))
								.setDefaultValue(HornSetting.DISABLED)
								.setSaveConsumer(setting -> config.setHornSetting(hornId, setting))
								.setEnumNameProvider(anEnum -> Component.translatable("text.autoconfig.indypets.option.goatHorn." + anEnum.name()))
								.build());
					}));
			} else {
				entries.add(
					ConfigEntryBuilder.create()
						.startTextDescription(Component.translatable(NOT_IN_WORLD))
						.setTooltip(Component.translatable(NOT_IN_WORLD + ".@Tooltip"))
						.build());
			}

			return entries;
		} catch (ClassCastException e) {
			IndyPets.LOGGER.error(e);
			return Collections.emptyList();
		}
	}
}
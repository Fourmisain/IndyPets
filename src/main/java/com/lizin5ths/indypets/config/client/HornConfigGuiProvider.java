package com.lizin5ths.indypets.config.client;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.HornSetting;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HornConfigGuiProvider implements GuiProvider {
	private static final String EXPLANATION = "text.autoconfig.indypets.option.goatHornExplanation";

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

			Registry.INSTRUMENT.getIds().stream()
				.sorted()
				.forEachOrdered(hornId -> {
					Text name = Text.translatable(Util.createTranslationKey("instrument", hornId));

					entries.add(
						ConfigEntryBuilder.create()
							.startEnumSelector(name, HornSetting.class, config.getHornSetting(hornId))
							.setDefaultValue(HornSetting.DISABLED)
							.setSaveConsumer(setting -> config.setHornSetting(hornId, setting))
							.setEnumNameProvider(anEnum -> Text.translatable("text.autoconfig.indypets.option.goatHorn." + anEnum.name()))
							.build()
					);
				});

			return entries;
		} catch (ClassCastException e) {
			IndyPets.LOGGER.error(e);
			return Collections.emptyList();
		}
	}
}
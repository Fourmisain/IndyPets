package com.lizin5ths.indypets.config.client;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.HornSetting;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HornConfigGuiProvider implements GuiProvider {
	private static final String EXPLANATION = "text.autoconfig.indypets.option.goatHornExplanation";

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
		try {
			Map<Identifier, HornSetting> hornConfig = (Map<Identifier, HornSetting>) field.get(config);
			List<AbstractConfigListEntry> entries = new ArrayList<>();

			entries.add(
				ConfigEntryBuilder.create()
					.startTextDescription(Text.translatable(EXPLANATION))
					.setTooltip(Text.translatable(EXPLANATION + ".@Tooltip"))
					.build());

			Registries.INSTRUMENT.getIds().stream()
				.sorted()
				.forEachOrdered(hornId -> {
					Text name = Text.translatable(Util.createTranslationKey("instrument", hornId));

					entries.add(
						ConfigEntryBuilder.create()
							.startEnumSelector(name, HornSetting.class, hornConfig.getOrDefault(hornId, HornSetting.DISABLED))
							.setDefaultValue(HornSetting.DISABLED)
							.setSaveConsumer(setting -> hornConfig.put(hornId, setting))
							.setEnumNameProvider(anEnum -> Text.translatable("text.autoconfig.indypets.option.goatHorn." + anEnum.name()))
							.build()
					);
				});

			return entries;
		} catch (IllegalAccessException e) {
			IndyPets.LOGGER.error(e);
			return Collections.emptyList();
		}
	}
}
package com.lizin5ths.indypets.config;

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static com.lizin5ths.indypets.config.Config.INNER_HOME_PERCENTAGE_DEFAULT;


public class InnerHomeGuiProvider implements GuiProvider {
	@SuppressWarnings("rawtypes")
	@Override
	public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
		int currentValue = (int) (Utils.<Float>getUnsafely(field, config) * 100f);
		int defaultValue = (int) (INNER_HOME_PERCENTAGE_DEFAULT * 100f);

		return Collections.singletonList(
			ConfigEntryBuilder.create()
				.startIntSlider(Text.translatable("text.autoconfig.indypets.option.innerHomePercentage"), currentValue, 0, 100)
				.setTooltip(Text.translatable("text.autoconfig.indypets.option.innerHomePercentage.@Tooltip"))
				.setDefaultValue(defaultValue)
				.setTextGetter(value -> Text.of(value + "%"))
				.setSaveConsumer(newValue -> Utils.setUnsafely(field, config, newValue / 100f))
				.build()
		);
	}
}

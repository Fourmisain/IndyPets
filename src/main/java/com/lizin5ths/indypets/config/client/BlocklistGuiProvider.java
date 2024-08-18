package com.lizin5ths.indypets.config.client;

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lizin5ths.indypets.config.BlocklistTypeAdapter.blocklistFromRaw;
import static com.lizin5ths.indypets.config.BlocklistTypeAdapter.blocklistToRaw;

public class BlocklistGuiProvider implements GuiProvider {
	@SuppressWarnings("rawtypes")
	@Override
	public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
		return Collections.singletonList(
			ConfigEntryBuilder.create()
				.startStrList(Text.translatable(i13n), blocklistToRaw(Utils.getUnsafely(field, config)))
				.setDefaultValue(() -> blocklistToRaw(Utils.getUnsafely(field, defaults)))
				.setSaveConsumer(newValue -> Utils.setUnsafely(field, config, blocklistFromRaw(newValue)))
				.setErrorSupplier(newValue -> {
					try {
						blocklistFromRaw(newValue);
						return Optional.empty();
					} catch (Exception e) {
						return Optional.of(Text.translatable("text.autoconfig.indypets.option.blocklist.error"));
					}
				})
				.build()
		);
	}
}

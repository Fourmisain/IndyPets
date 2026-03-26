package com.lizin5ths.indypets.config.client;

import com.lizin5ths.indypets.IndyPets;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemGuiProvider implements GuiProvider {
	// Cloth Config understands null as an error, thus we use a "null object"
	private static final Identifier NULL = IndyPets.id( "no_item");

	public static Identifier toObject(String id) {
		if (id.isEmpty())
			return NULL; // translate to null later

		Identifier identifier = Identifier.tryParse(id);

		if (!BuiltInRegistries.ITEM.containsKey(identifier))
			return null; // show as error

		return identifier;
	}

	public static Identifier get(Field field, Object config) {
		Identifier id = Utils.getUnsafely(field, config);

		if (id == null)
			return NULL;

		return id;
	}

	public static void set(Field field, Object config, Identifier newValue) {
		if (newValue == NULL)
			newValue = null;

		Utils.setUnsafely(field, config, newValue);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
		return Collections.singletonList(
			ConfigEntryBuilder.create()
				.startDropdownMenu(Component.translatable("text.autoconfig.indypets.option.interactItem"),
					DropdownMenuBuilder.TopCellElementBuilder.of(
						ItemGuiProvider.get(field, config),
						ItemGuiProvider::toObject,
						value -> Component.literal(value == NULL ? "" : value.toString())),
					DropdownMenuBuilder.CellCreatorBuilder.ofItemIdentifier())
				.setDefaultValue(NULL)
				.setSelections(
					Stream.concat(
							BuiltInRegistries.ITEM.stream()
								.sorted(Comparator.comparing(Item::toString))
								.map(BuiltInRegistries.ITEM::getKey),
							Stream.of(NULL))
						.collect(Collectors.toCollection(LinkedHashSet::new)))
				.setSaveConsumer(newValue -> set(field, config, newValue))
				.build()
		);
	}
}

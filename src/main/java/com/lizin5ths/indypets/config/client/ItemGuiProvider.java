package com.lizin5ths.indypets.config.client;

import com.lizin5ths.indypets.IndyPets;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ItemGuiProvider implements GuiProvider {
	// Cloth Config understands null as an error, thus we use a "null object"
	private static final Identifier NULL = IndyPets.id( "no_item");

	public static Identifier toObject(String id) {
		if (id.isEmpty())
			return NULL; // translate to null later

		var identifier = Identifier.tryParse(id);

		if (identifier == null || !BuiltInRegistries.ITEM.containsKey(identifier))
			return null; // show as error

		return identifier;
	}

	private static String toText(Identifier id) {
		return id == NULL ? "" : id.toString();
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
		var description = Component.translatable("text.autoconfig.indypets.option.interactItem");
		var value = get(field, config);
		var selections = BuiltInRegistries.ITEM.stream()
			.sorted(Comparator.comparing(Item::toString))
			.map(BuiltInRegistries.ITEM::getKey)
			.collect(Collectors.toCollection(LinkedHashSet::new));

		if (Minecraft.getInstance().level != null) {
			return Collections.singletonList(
				ConfigEntryBuilder.create()
					.startDropdownMenu(description,
						DropdownMenuBuilder.TopCellElementBuilder.of(
							value,
							ItemGuiProvider::toObject,
							v -> Component.literal(toText(v))),
						DropdownMenuBuilder.CellCreatorBuilder.ofItemIdentifier())
					.setDefaultValue(NULL)
					.setSelections(selections)
					.setSaveConsumer(newValue -> set(field, config, newValue))
					.build()
			);
		} else {
			// workaround for broken item dropdown in 26.1 when no world is loaded
			return Collections.singletonList(
				ConfigEntryBuilder.create()
					.startStringDropdownMenu(description,
						toText(value),
						Component::literal)
					.setDefaultValue(toText(NULL))
					.setSelections(selections.stream().map(ItemGuiProvider::toText).toList())
					.setSaveConsumer(newValue -> 	set(field, config, toObject(newValue)))
					.build()
			);
		}
	}
}

package com.lizin5ths.indypets.config;

import net.minecraft.util.Identifier;

import java.util.LinkedHashSet;
import java.util.Set;

public class Blocklist {
	public final Set<Identifier> idBlocklist = new LinkedHashSet<>(); // specific pets
	public final Set<String> modBlocklist = new LinkedHashSet<>(); // specific mods

	public static Blocklist getDefault() {
		Blocklist blocklist = new Blocklist();

		// capybaras change between sitting/standing by sneak-interacting with a stick
//		blocklist.idBlocklist.add(Identifier.of("capybara", "capybara"));

		// snails don't have follow state
		blocklist.idBlocklist.add(Identifier.of("lovely_snails", "snail"));

		// mounts by default don't follow their owner and some have sneak-interactions
		blocklist.modBlocklist.add("mythicmounts");

		return blocklist;
	}

	public static Blocklist getInteractDefault() {
		Blocklist blocklist = new Blocklist();

		// Cobblemon have a custom sneak interact menu that would be blocked by this mod
		blocklist.modBlocklist.add("cobblemon");

		return blocklist;
	}

	public boolean isBlocked(Identifier petId) {
		if (modBlocklist.contains(petId.getNamespace())) return true;
		return idBlocklist.contains(petId);
	}
}

package com.lizin5ths.indypets.config;

import net.minecraft.util.StringIdentifiable;

public enum HornSetting implements StringIdentifiable {
	DISABLED("disabled"),
	WHISTLE("whistle"),
	UNWHISTLE("unwhistle"),
	TOGGLE("toggle"),
	WHISTLE_OR_SNEAK_UNWHISTLE("whistle_or_sneak_unwhistle");

	private final String id;

	HornSetting(String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return id;
	}
}

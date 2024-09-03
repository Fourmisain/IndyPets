package com.lizin5ths.indypets.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum HornSetting implements StringIdentifiable {
	DISABLED("disabled"),
	WHISTLE("whistle"),
	UNWHISTLE("unwhistle"),
	TOGGLE("toggle");

	public static final Codec<HornSetting> CODEC = StringIdentifiable.createCodec(HornSetting::values);

	private final String id;

	HornSetting(String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return id;
	}
}

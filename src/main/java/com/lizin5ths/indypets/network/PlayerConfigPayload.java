package com.lizin5ths.indypets.network;

import com.google.gson.JsonSyntaxException;
import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PlayerConfigPayload(Config config) implements CustomPayload {
	public static final Id<PlayerConfigPayload> ID = new CustomPayload.Id<>(IndyPets.id("player_config"));

	public static final PacketCodec<PacketByteBuf, PlayerConfigPayload> CODEC = PacketCodec.of(
		(value, buf) -> buf.writeString(Config.GSON.toJson(Config.LOCAL_CONFIG)),
		buf -> {
			String json = buf.readString(32767);
			try {
				Config config = Config.GSON.fromJson(json, Config.class);
				return new PlayerConfigPayload(config);
			} catch (JsonSyntaxException e) {
				IndyPets.LOGGER.error("couldn't parse received player config! \"{}\"", json, e);
				return null;
			}
		});

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

package com.lizin5ths.indypets.network;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PlayerConfigPayload(Config config) implements CustomPayload {
	public static final Id<PlayerConfigPayload> ID = new CustomPayload.Id<>(IndyPets.id("player_config"));

	public static final PacketCodec<PacketByteBuf, PlayerConfigPayload> CODEC = PacketCodec.of(
		(payload, buf) -> {
			String json = Config.GSON_EXCLUDE_LOCAL.toJson(payload.config());
			buf.writeString(json);
		},
		buf -> {
			String json = buf.readString(32767);
			Config config = Config.GSON_EXCLUDE_LOCAL.fromJson(json, Config.class);
			return new PlayerConfigPayload(config);
		});

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

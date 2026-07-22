package com.lizin5ths.indypets.network;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PlayerConfigPayload(Config config) implements CustomPacketPayload {
	public static final Type<PlayerConfigPayload> TYPE = new CustomPacketPayload.Type<>(IndyPets.id("player_config"));

	public static final StreamCodec<FriendlyByteBuf, PlayerConfigPayload> CODEC = StreamCodec.ofMember(
		(payload, buf) -> {
			String json = Config.GSON_EXCLUDE_LOCAL.toJson(payload.config());
			buf.writeUtf(json);
		},
		buf -> {
			String json = buf.readUtf(32767);
			Config config = Config.GSON_EXCLUDE_LOCAL.fromJson(json, Config.class);
			return new PlayerConfigPayload(config);
		});

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

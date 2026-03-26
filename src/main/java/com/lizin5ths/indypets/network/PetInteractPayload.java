package com.lizin5ths.indypets.network;

import com.lizin5ths.indypets.IndyPets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PetInteractPayload(int entityId) implements CustomPacketPayload {
	public static final Type<PetInteractPayload> ID = new CustomPacketPayload.Type<>(IndyPets.id("interact"));

	public static final StreamCodec<FriendlyByteBuf, PetInteractPayload> CODEC = StreamCodec.ofMember(
		(value, buf) -> buf.writeVarInt(value.entityId()),
		buf -> new PetInteractPayload(buf.readVarInt()));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}

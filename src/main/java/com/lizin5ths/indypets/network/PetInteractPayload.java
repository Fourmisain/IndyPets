package com.lizin5ths.indypets.network;

import com.lizin5ths.indypets.IndyPets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PetInteractPayload(int entityId) implements CustomPayload {
	public static final Id<PetInteractPayload> ID = CustomPayload.id(IndyPets.id("interact").toString());

	public static final PacketCodec<PacketByteBuf, PetInteractPayload> CODEC = PacketCodec.of(
		(value, buf) -> buf.writeVarInt(value.entityId()),
		buf -> new PetInteractPayload(buf.readVarInt()));

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

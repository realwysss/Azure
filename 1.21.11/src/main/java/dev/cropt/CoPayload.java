package dev.cropt;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CoPayload(boolean ok) implements CustomPacketPayload {
	public static final Type<CoPayload> T = new Type<>(Identifier.fromNamespaceAndPath("crystalopt", "control"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CoPayload> C = StreamCodec.composite(ByteBufCodecs.BOOL, CoPayload::ok, CoPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return T;
	}
}

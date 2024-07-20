package com.plusls.MasaGadget.impl.network.packet;

import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundDisablePcaSyncProtocolPacket() implements CustomPacketPayload {
    public static final Type<ClientboundDisablePcaSyncProtocolPacket> TYPE = new Type<>(PcaSyncProtocol.DISABLE_PCA_SYNC_PROTOCOL);
    public static final StreamCodec<FriendlyByteBuf, ClientboundDisablePcaSyncProtocolPacket> CODEC = CustomPacketPayload.codec(ClientboundDisablePcaSyncProtocolPacket::write, ClientboundDisablePcaSyncProtocolPacket::new);

    public ClientboundDisablePcaSyncProtocolPacket(FriendlyByteBuf byteBuf) {
        this();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ClientboundDisablePcaSyncProtocolPacket.TYPE;
    }

    private void write(FriendlyByteBuf byteBuf) {
    }
}

package com.plusls.MasaGadget.impl.network.packet;

import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundEnablePcaSyncProtocolPacket() implements CustomPacketPayload {
    public static final Type<ClientboundEnablePcaSyncProtocolPacket> TYPE = new Type<>(PcaSyncProtocol.ENABLE_PCA_SYNC_PROTOCOL);
    public static final StreamCodec<FriendlyByteBuf, ClientboundEnablePcaSyncProtocolPacket> CODEC = CustomPacketPayload.codec(ClientboundEnablePcaSyncProtocolPacket::write, ClientboundEnablePcaSyncProtocolPacket::new);

    public ClientboundEnablePcaSyncProtocolPacket(FriendlyByteBuf byteBuf) {
        this();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ClientboundEnablePcaSyncProtocolPacket.TYPE;
    }

    private void write(FriendlyByteBuf byteBuf) {
    }
}

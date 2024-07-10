package com.plusls.MasaGadget.impl.network.packet;

import com.plusls.MasaGadget.api.network.packet.PCAPacket;
import com.plusls.MasaGadget.impl.network.NoopCodec;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ClientboundUpdateEntityPacket extends PCAPacket {
    public static final Type<PCAPacket> TYPE = new Type<>(PcaSyncProtocol.UPDATE_ENTITY);
    public static final StreamCodec<FriendlyByteBuf, PCAPacket> CODEC = NoopCodec.create(ClientboundUpdateEntityPacket.transform());

    public ClientboundUpdateEntityPacket(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    public static void handle(PCAPacket packet, ClientPlayNetworking.Context context) {
        PcaSyncProtocol.updateEntityHandler(context.client(), context.client().getConnection(), packet.getByteBuf(), context.responseSender());
    }

    private static Function<FriendlyByteBuf, PCAPacket> transform() {
        return ClientboundUpdateEntityPacket::new;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ClientboundUpdateEntityPacket.TYPE;
    }
}

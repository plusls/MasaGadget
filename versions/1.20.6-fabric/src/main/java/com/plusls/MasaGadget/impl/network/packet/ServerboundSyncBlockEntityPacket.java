package com.plusls.MasaGadget.impl.network.packet;

import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundSyncBlockEntityPacket(FriendlyByteBuf buf) implements CustomPacketPayload {
    public static final Type<ServerboundSyncBlockEntityPacket> TYPE = new Type<>(PcaSyncProtocol.SYNC_ENTITY);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ServerboundSyncBlockEntityPacket.TYPE;
    }
}

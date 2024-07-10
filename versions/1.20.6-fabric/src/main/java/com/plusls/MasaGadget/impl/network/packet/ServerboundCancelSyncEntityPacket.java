package com.plusls.MasaGadget.impl.network.packet;

import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundCancelSyncEntityPacket(FriendlyByteBuf buf) implements CustomPacketPayload {
    public static final Type<ServerboundCancelSyncEntityPacket> TYPE = new Type<>(PcaSyncProtocol.CANCEL_SYNC_ENTITY);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ServerboundCancelSyncEntityPacket.TYPE;
    }
}

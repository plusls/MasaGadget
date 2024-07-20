package com.plusls.MasaGadget.impl.network.packet;

import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundSyncEntityPacket(int entityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundSyncEntityPacket> TYPE = new CustomPacketPayload.Type<>(PcaSyncProtocol.SYNC_ENTITY);
    public static final StreamCodec<FriendlyByteBuf, ServerboundSyncEntityPacket> CODEC = CustomPacketPayload.codec(ServerboundSyncEntityPacket::write, ServerboundSyncEntityPacket::new);

    public ServerboundSyncEntityPacket(@NotNull FriendlyByteBuf byteBuf) {
        this(byteBuf.readInt());
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ServerboundSyncEntityPacket.TYPE;
    }

    private void write(@NotNull FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(this.entityId);
    }
}

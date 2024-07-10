package com.plusls.MasaGadget.api.network.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Getter
@AllArgsConstructor
public abstract class PCAPacket implements CustomPacketPayload {
    private final FriendlyByteBuf byteBuf;
}

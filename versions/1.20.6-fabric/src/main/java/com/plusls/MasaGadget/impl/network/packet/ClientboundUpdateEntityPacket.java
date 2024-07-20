package com.plusls.MasaGadget.impl.network.packet;

import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.util.minecraft.NetworkUtil;

public record ClientboundUpdateEntityPacket(ResourceLocation dimension, int entityId, CompoundTag tag) implements CustomPacketPayload {
    public static final Type<ClientboundUpdateEntityPacket> TYPE = new Type<>(PcaSyncProtocol.UPDATE_ENTITY);
    public static final StreamCodec<FriendlyByteBuf, ClientboundUpdateEntityPacket> CODEC = CustomPacketPayload.codec(ClientboundUpdateEntityPacket::write, ClientboundUpdateEntityPacket::new);

    public ClientboundUpdateEntityPacket(@NotNull FriendlyByteBuf byteBuf) {
        this(byteBuf.readResourceLocation(), byteBuf.readInt(), NetworkUtil.readNbt(byteBuf));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ClientboundUpdateEntityPacket.TYPE;
    }

    private void write(@NotNull FriendlyByteBuf byteBuf) {
        byteBuf.writeResourceLocation(this.dimension);
        byteBuf.writeInt(this.entityId);
        byteBuf.writeNbt(this.tag);
    }
}

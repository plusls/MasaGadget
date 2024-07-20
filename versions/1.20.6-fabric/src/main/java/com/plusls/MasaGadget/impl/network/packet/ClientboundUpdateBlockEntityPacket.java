package com.plusls.MasaGadget.impl.network.packet;

import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.util.minecraft.NetworkUtil;

public record ClientboundUpdateBlockEntityPacket(ResourceLocation dimension, BlockPos blockPos, CompoundTag tag) implements CustomPacketPayload {
    public static final Type<ClientboundUpdateBlockEntityPacket> TYPE = new Type<>(PcaSyncProtocol.UPDATE_BLOCK_ENTITY);
    public static final StreamCodec<FriendlyByteBuf, ClientboundUpdateBlockEntityPacket> CODEC = CustomPacketPayload.codec(ClientboundUpdateBlockEntityPacket::write, ClientboundUpdateBlockEntityPacket::new);

    public ClientboundUpdateBlockEntityPacket(@NotNull FriendlyByteBuf byteBuf) {
        this(byteBuf.readResourceLocation(), byteBuf.readBlockPos(), NetworkUtil.readNbt(byteBuf));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ClientboundUpdateBlockEntityPacket.TYPE;
    }

    private void write(@NotNull FriendlyByteBuf byteBuf) {
        byteBuf.writeResourceLocation(this.dimension);
        byteBuf.writeBlockPos(this.blockPos);
        byteBuf.writeNbt(this.tag);
    }
}

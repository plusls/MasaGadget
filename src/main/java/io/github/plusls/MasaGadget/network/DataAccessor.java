package io.github.plusls.MasaGadget.network;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;


public class DataAccessor {
    static public BlockPos lastBlockPos = null;

    @Environment(EnvType.CLIENT)
    static public void requestBlockEntity(BlockPos pos) {
        if (!MasaGadgetMod.masaGagdetInServer) {
            return;
        }
        if (MinecraftClient.getInstance().getNetworkHandler() == null) {
            return;
        }
        if (lastBlockPos != null && lastBlockPos.equals(pos)) {
            return;
        }
        MasaGadgetMod.LOGGER.debug("requestBlockEntity: {}", pos);
        lastBlockPos = pos;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(true);
        buf.writeBlockPos(pos);
        ClientSidePacketRegistry.INSTANCE.sendToServer(ServerNetworkHandler.REQUEST_BLOCK_ENTITY, buf);
    }
}

package io.github.plusls.MasaGadget.network;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class DataAccessor {
    static public BlockPos lastBlockPos = null;
    static public int lastEntityId = -1;

    static public void requestBlockEntity(BlockPos pos) {
        if (!MasaGadgetMod.masaGagdetInServer) {
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

    static public void requestEntity(int entityId) {
        if (!MasaGadgetMod.masaGagdetInServer) {
            return;
        }
        if (lastEntityId != -1 && lastEntityId == entityId) {
            return;
        }
        MasaGadgetMod.LOGGER.debug("requestEntity: {}", entityId);
        lastEntityId = entityId;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(true);
        buf.writeInt(entityId);
        ClientSidePacketRegistry.INSTANCE.sendToServer(ServerNetworkHandler.REQUEST_ENTITY, buf);
    }
}

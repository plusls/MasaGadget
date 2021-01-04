package io.github.plusls.MasaGadget.network;


import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ServerNetworkHandler {

    private static final Identifier SYNC_BLOCK_ENTITY = new Identifier("plusls_carpet_addition_mod:sync_block_entity");
    private static final Identifier SYNC_ENTITY = new Identifier("plusls_carpet_addition_mod:sync_entity");
    private static final Identifier CANCEL_SYNC_REQUEST_BLOCK_ENTITY = new Identifier("plusls_carpet_addition_mod:cancel_sync_block_entity");
    private static final Identifier CANCEL_SYNC_ENTITY = new Identifier("plusls_carpet_addition_mod:cancel_sync_entity");

    static public BlockPos lastBlockPos = null;
    static public int lastEntityId = -1;


    static public void syncBlockEntity(BlockPos pos) {
        if (lastBlockPos != null && lastBlockPos.equals(pos)) {
            return;
        }
        MasaGadgetMod.LOGGER.debug("syncBlockEntity: {}", pos);
        lastBlockPos = pos;
        lastEntityId = -1;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        ClientPlayNetworking.send(SYNC_BLOCK_ENTITY, buf);
    }

    static public void syncEntity(int entityId) {
        if (lastEntityId != -1 && lastEntityId == entityId) {
            return;
        }
        MasaGadgetMod.LOGGER.debug("syncEntity: {}", entityId);
        lastEntityId = entityId;
        lastBlockPos = null;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        ClientPlayNetworking.send(SYNC_ENTITY, buf);
    }

    static public void cancelSyncBlockEntity() {
        if (ServerNetworkHandler.lastBlockPos == null) {
            return;
        }
        lastBlockPos = null;
        MasaGadgetMod.LOGGER.debug("cancelSyncBlockEntity.");
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(CANCEL_SYNC_REQUEST_BLOCK_ENTITY, buf);
    }

    static public void cancelSyncEntity() {
        if (lastEntityId == -1) {
            return;
        }
        lastEntityId = -1;
        MasaGadgetMod.LOGGER.debug("cancelSyncEntity.");
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(CANCEL_SYNC_ENTITY, buf);
    }

}
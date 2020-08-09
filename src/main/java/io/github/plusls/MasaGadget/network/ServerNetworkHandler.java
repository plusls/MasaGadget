package io.github.plusls.MasaGadget.network;


import io.github.plusls.MasaGadget.MasaGadgetMod;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerNetworkHandler {

    public static final Identifier REQUEST_BLOCK_ENTITY = MasaGadgetMod.id("request_block_entity");
    public static Map<UUID, BlockPos> lastBlockPosMap = new HashMap<>();

    public static void init() {
        ServerSidePacketRegistry.INSTANCE.register(REQUEST_BLOCK_ENTITY, ServerNetworkHandler::requestBlockEntityHandler);
    }

    private static void requestBlockEntityHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        ServerPlayerEntity player = (ServerPlayerEntity) packetContext.getPlayer();
        ServerWorld world = (ServerWorld) player.world;
        if (packetByteBuf.readBoolean()) {
            BlockPos pos = packetByteBuf.readBlockPos();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            MasaGadgetMod.LOGGER.debug("watch blockpos! {}: {}", pos, blockEntity);
            MasaGadgetMod.LOGGER.debug("mark update!");
            world.getChunkManager().markForUpdate(pos);
            lastBlockPosMap.put(player.getUuid(), pos);
        } else {
            MasaGadgetMod.LOGGER.debug("cancel watch blockpos!");
            lastBlockPosMap.remove(player.getUuid());
        }
    }
}
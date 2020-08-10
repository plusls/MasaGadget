package io.github.plusls.MasaGadget.network;


import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
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
    public static final Identifier REQUEST_ENTITY = MasaGadgetMod.id("request_entity");

    public static Map<UUID, BlockPos> lastBlockPosMap = new HashMap<>();
    public static Map<UUID, Integer> lastEntityUuidMap = new HashMap<>();

    public static void init() {
        ServerSidePacketRegistry.INSTANCE.register(REQUEST_BLOCK_ENTITY, ServerNetworkHandler::requestBlockEntityHandler);
        ServerSidePacketRegistry.INSTANCE.register(REQUEST_ENTITY, ServerNetworkHandler::requestEntityHandler);
    }

    private static void requestBlockEntityHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        ServerPlayerEntity player = (ServerPlayerEntity) packetContext.getPlayer();
        ServerWorld world = (ServerWorld) player.world;
        if (packetByteBuf.readBoolean()) {
            BlockPos pos = packetByteBuf.readBlockPos();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            // 因为非主线程这里获取到的 blockEntity 总为空，但是无所谓，只需要标记更新
            MasaGadgetMod.LOGGER.debug("watch blockpos! {}: {}", pos, blockEntity);
            MasaGadgetMod.LOGGER.debug("mark update!");
            world.getChunkManager().markForUpdate(pos);
            lastBlockPosMap.put(player.getUuid(), pos);
        } else {
            MasaGadgetMod.LOGGER.debug("cancel watch blockpos!");
            lastBlockPosMap.remove(player.getUuid());
        }
    }

    private static void requestEntityHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        ServerPlayerEntity player = (ServerPlayerEntity) packetContext.getPlayer();
        ServerWorld world = (ServerWorld) player.world;
        if (packetByteBuf.readBoolean()) {
            int entityId = packetByteBuf.readInt();
            Entity entity = world.getEntityById(entityId);
            MasaGadgetMod.LOGGER.debug("watch entity! {}: {}", entityId, entity);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(entityId);
            buf.writeCompoundTag(entity.toTag(new CompoundTag()));
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ClientNetworkHandler.RESPONSE_ENTITY, buf);
            lastEntityUuidMap.put(player.getUuid(), entityId);
        } else {
            lastEntityUuidMap.remove(player.getUuid());
        }
    }
}
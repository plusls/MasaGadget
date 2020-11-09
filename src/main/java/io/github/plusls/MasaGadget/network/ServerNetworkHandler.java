package io.github.plusls.MasaGadget.network;


import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.netty.buffer.Unpooled;
import net.earthcomputer.multiconnect.api.IIdentifierCustomPayloadListener;
import net.earthcomputer.multiconnect.api.MultiConnectAPI;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
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

public class ServerNetworkHandler implements IIdentifierCustomPayloadListener {

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
            BlockState blockState = world.getBlockState(pos);
            MasaGadgetMod.LOGGER.debug("watch blockpos! {}: {}", pos, blockState);
            // 本来想判断一下 blockState 类型做个白名单的，考虑到 client 已经做了判断就不在服务端做判断了
            // 就算被恶意攻击应该不会造成什么损失
            // 大不了 op 直接拉黑
            world.getChunkManager().markForUpdate(pos);
            // 不是单个箱子则需要更新隔壁箱子
            if (blockState.getBlock() instanceof ChestBlock && blockState.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                BlockPos posAdj = pos.offset(ChestBlock.getFacing(blockState));
                world.getChunkManager().markForUpdate(posAdj);
            }
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

    @Override
    public void onCustomPayload(int protocol, Identifier channel, PacketByteBuf data) {
        while (true) {
            try {
                if (channel.equals(ServerNetworkHandler.REQUEST_BLOCK_ENTITY) || channel.equals(ServerNetworkHandler.REQUEST_ENTITY)) {
                    MasaGadgetMod.LOGGER.debug("forceSendCustomPayload: masagadget");
                    MultiConnectAPI.instance().forceSendCustomPayload(channel, data);
                } else if (channel.equals(new Identifier("bbor:subscribe"))) {
                    MasaGadgetMod.LOGGER.debug("forceSendCustomPayload: bbor");
                    MultiConnectAPI.instance().forceSendCustomPayload(channel, data);
                }
            } catch (IllegalStateException e) {
                // MinecraftClient.getInstance().getNetworkHandler() 在游戏刚连接时会返回 null
                // 由于 multiconnect mixin 了 ClientPlayNetworkHandler.sendPacket
                // 因此只能用死循环来缓解这个问题
                // 不知道有没有优雅的解决方案
                continue;
            }
            break;
        }
    }
}
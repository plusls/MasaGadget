package com.plusls.MasaGadget.tweakeroo.pcaSyncProtocol;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.DisconnectEvent;
import com.plusls.MasaGadget.litematica.saveInventoryToSchematicInServer.PcaSyncUtil;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class PcaSyncProtocol {
    private static final String NAMESPACE = "pca";

    private static Identifier id(String path) {
        return new Identifier(NAMESPACE, path);
    }

    // send
    private static final Identifier SYNC_BLOCK_ENTITY = id("sync_block_entity");
    private static final Identifier SYNC_ENTITY = id("sync_entity");
    private static final Identifier CANCEL_SYNC_REQUEST_BLOCK_ENTITY = id("cancel_sync_block_entity");
    private static final Identifier CANCEL_SYNC_ENTITY = id("cancel_sync_entity");

    // recv
    private static final Identifier ENABLE_PCA_SYNC_PROTOCOL = id("enable_pca_sync_protocol");
    private static final Identifier DISABLE_PCA_SYNC_PROTOCOL = id("disable_pca_sync_protocol");
    private static final Identifier UPDATE_ENTITY = id("update_entity");
    private static final Identifier UPDATE_BLOCK_ENTITY = id("update_block_entity");

    private static BlockPos lastBlockPos = null;
    private static int lastEntityId = -1;
    public static boolean enable = false;

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ENABLE_PCA_SYNC_PROTOCOL, PcaSyncProtocol::enablePcaSyncProtocolHandle);
        ClientPlayNetworking.registerGlobalReceiver(DISABLE_PCA_SYNC_PROTOCOL, PcaSyncProtocol::disablePcaSyncProtocolHandle);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_ENTITY, PcaSyncProtocol::updateEntityHandler);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_BLOCK_ENTITY, PcaSyncProtocol::updateBlockEntityHandler);
        // 该事件仅在服务器主动断开客户端发生
        // ClientPlayConnectionEvents.DISCONNECT.register(PcaSyncProtocol::onDisconnect);
        DisconnectEvent.register(PcaSyncProtocol::onDisconnect);
    }


    private static void onDisconnect() {
        ModInfo.LOGGER.info("pcaSyncProtocol onDisconnect.");
        enable = false;
    }

    private static void enablePcaSyncProtocolHandle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (!client.isIntegratedServerRunning()) {
            ModInfo.LOGGER.info("pcaSyncProtocol enable.");
            enable = true;
        }
    }

    private static void disablePcaSyncProtocolHandle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (!client.isIntegratedServerRunning()) {
            ModInfo.LOGGER.info("pcaSyncProtocol disable.");
            enable = false;
        }
    }

    // 反序列化实体数据
    private static void updateEntityHandler(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        World world = player.world;
        if (!Objects.equals(DimensionType.getId(world.getDimension().getType()), buf.readIdentifier())) {
            return;
        }
        int entityId = buf.readInt();
        CompoundTag tag = buf.readCompoundTag();
        Entity entity = world.getEntityById(entityId);

        if (entity != null) {
            ModInfo.LOGGER.debug("update entity!");
            assert tag != null;
            if (entity instanceof StorageMinecartEntity) {
                ((StorageMinecartEntity) entity).inventory.clear();
                Inventories.fromTag(tag, ((StorageMinecartEntity) entity).inventory);
            } else if (entity instanceof AbstractTraderEntity) {
                ((AbstractTraderEntity) entity).getInventory().clear();
                ListTag listTag = tag.getList("Inventory", 10);
                for(int i = 0; i < listTag.size(); ++i) {
                    ItemStack itemStack = ItemStack.fromTag(listTag.getCompound(i));
                    if (!itemStack.isEmpty()) {
                        ((AbstractTraderEntity) entity).getInventory().add(itemStack);
                    }
                }
                ((AbstractTraderEntity) entity).offers = new TraderOfferList(tag.getCompound("Offers"));
            } else if (entity instanceof HorseBaseEntity) {
                // TODO 写的更优雅一些
                entity.fromTag(tag);
            } else if (entity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity) entity;
                playerEntity.inventory.deserialize(tag.getList("Inventory", 10));
                if (tag.contains("EnderItems", 9)) {
                    playerEntity.getEnderChestInventory().readTags(tag.getList("EnderItems", 10));
                }
            }
        }
    }

    // 反序列化 blockEntity 数据
    private static void updateBlockEntityHandler(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        World world = player.world;
        if (!Objects.equals(DimensionType.getId(world.getDimension().getType()), buf.readIdentifier())) {
            return;
        }
        BlockPos pos = buf.readBlockPos();
        CompoundTag tag = buf.readCompoundTag();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (Configs.Litematica.SAVE_INVENTORY_TO_SCHEMATIC_IN_SERVER.getBooleanValue() && pos.equals(PcaSyncUtil.lastUpdatePos)) {
            InfoUtils.showGuiOrInGameMessage(Message.MessageType.SUCCESS, "masa_gadget_mod.message.loadInventoryToLocalSuccess");
        }
        if (blockEntity != null) {
            ModInfo.LOGGER.debug("update blockEntity!");
            blockEntity.fromTag(tag);
        }
    }


    static public void syncBlockEntity(BlockPos pos) {
        if (lastBlockPos != null && lastBlockPos.equals(pos)) {
            return;
        }
        ModInfo.LOGGER.debug("syncBlockEntity: {}", pos);
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
        ModInfo.LOGGER.debug("syncEntity: {}", entityId);
        lastEntityId = entityId;
        lastBlockPos = null;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        ClientPlayNetworking.send(SYNC_ENTITY, buf);
    }

    static public void cancelSyncBlockEntity() {
        if (lastBlockPos == null) {
            return;
        }
        lastBlockPos = null;
        ModInfo.LOGGER.debug("cancelSyncBlockEntity.");
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(CANCEL_SYNC_REQUEST_BLOCK_ENTITY, buf);
    }

    static public void cancelSyncEntity() {
        if (lastEntityId == -1) {
            return;
        }
        lastEntityId = -1;
        ModInfo.LOGGER.debug("cancelSyncEntity.");
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(CANCEL_SYNC_ENTITY, buf);
    }
}

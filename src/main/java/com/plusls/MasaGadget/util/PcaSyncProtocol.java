package com.plusls.MasaGadget.util;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.DisconnectEvent;
import com.plusls.MasaGadget.litematica.saveInventoryToSchematicInServer.PcaSyncUtil;
import com.plusls.MasaGadget.mixin.accessor.AccessorAbstractMinecartContainer;
import com.plusls.MasaGadget.mixin.accessor.AccessorAbstractVillager;
import com.plusls.MasaGadget.mixin.accessor.AccessorVillager;
import com.plusls.MasaGadget.mixin.accessor.AccessorZombieVillager;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PcaSyncProtocol {
    private static final String NAMESPACE = "pca";
    // send
    private static final ResourceLocation SYNC_BLOCK_ENTITY = id("sync_block_entity");
    private static final ResourceLocation SYNC_ENTITY = id("sync_entity");
    private static final ResourceLocation CANCEL_SYNC_REQUEST_BLOCK_ENTITY = id("cancel_sync_block_entity");
    private static final ResourceLocation CANCEL_SYNC_ENTITY = id("cancel_sync_entity");
    // recv
    private static final ResourceLocation ENABLE_PCA_SYNC_PROTOCOL = id("enable_pca_sync_protocol");
    private static final ResourceLocation DISABLE_PCA_SYNC_PROTOCOL = id("disable_pca_sync_protocol");
    private static final ResourceLocation UPDATE_ENTITY = id("update_entity");
    private static final ResourceLocation UPDATE_BLOCK_ENTITY = id("update_block_entity");
    //    private static final ClientboundIdentifierCustomPayloadListener clientboundIdentifierCustomPayloadListener =
//            new ClientboundIdentifierCustomPayloadListener();
//    private static final ServerboundIdentifierCustomPayloadListener serverboundIdentifierCustomPayloadListener =
//            new ServerboundIdentifierCustomPayloadListener();
    public static boolean enable = false;
    private static BlockPos lastBlockPos = null;
    private static int lastEntityId = -1;

    private static ResourceLocation id(String path) {
        return new ResourceLocation(NAMESPACE, path);
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ENABLE_PCA_SYNC_PROTOCOL, PcaSyncProtocol::enablePcaSyncProtocolHandle);
        ClientPlayNetworking.registerGlobalReceiver(DISABLE_PCA_SYNC_PROTOCOL, PcaSyncProtocol::disablePcaSyncProtocolHandle);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_ENTITY, PcaSyncProtocol::updateEntityHandler);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_BLOCK_ENTITY, PcaSyncProtocol::updateBlockEntityHandler);
        // 该事件仅在服务器主动断开客户端发生
        // ClientPlayConnectionEvents.DISCONNECT.register(PcaSyncProtocol::onDisconnect);
        DisconnectEvent.register(PcaSyncProtocol::onDisconnect);
        // TODO
        //MultiConnectAPI.instance().addClientboundIdentifierCustomPayloadListener(clientboundIdentifierCustomPayloadListener);
        //MultiConnectAPI.instance().addServerboundIdentifierCustomPayloadListener(serverboundIdentifierCustomPayloadListener);
    }

    private static void onDisconnect() {
        ModInfo.LOGGER.info("pcaSyncProtocol onDisconnect.");
        enable = false;
    }

    private static void enablePcaSyncProtocolHandle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (!client.hasSingleplayerServer()) {
            ModInfo.LOGGER.info("pcaSyncProtocol enable.");
            enable = true;
        }
    }

    private static void disablePcaSyncProtocolHandle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (!client.hasSingleplayerServer()) {
            ModInfo.LOGGER.info("pcaSyncProtocol disable.");
            enable = false;
        }
    }

    // 反序列化实体数据
    private static void updateEntityHandler(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        Level world = player.level;
        if (!world.getDimensionLocation().equals(buf.readResourceLocation())) {
            return;
        }
        int entityId = buf.readInt();
        CompoundTag tag = buf.readNbt();
        Entity entity = world.getEntity(entityId);

        if (entity != null) {
            ModInfo.LOGGER.debug("update entity!");
            assert tag != null;
            if (entity instanceof Mob) {
                if (tag.getBoolean("PersistenceRequired")) {
                    ((Mob) entity).setPersistenceRequired();
                }
            }
            if (entity instanceof AbstractMinecartContainer) {
                NonNullList<ItemStack> itemStacks = ((AccessorAbstractMinecartContainer) entity).getItemStacks();
                itemStacks.clear();
                ContainerHelper.loadAllItems(tag, itemStacks);
            } else if (entity instanceof AbstractVillager) {
                ((AbstractVillager) entity).getInventory().clearContent();
                ((AbstractVillager) entity).getInventory().fromTag(tag.getList("Inventory", Tag.TAG_COMPOUND));
                ((AccessorAbstractVillager) entity).setOffers(new MerchantOffers(tag.getCompound("Offers")));
                if (entity instanceof Villager) {
                    ((AccessorVillager) entity).setNumberOfRestocksToday(tag.getInt("RestocksToday"));
                    ((AccessorVillager) entity).setLastRestockGameTime(tag.getLong("LastRestock"));
                }
            } else if (entity instanceof AbstractHorse) {
                // TODO 写的更优雅一些
                entity.load(tag);
            } else if (entity instanceof Player) {
                Player playerEntity = (Player) entity;
                playerEntity.getInventory().load(tag.getList("Inventory", Tag.TAG_COMPOUND));
                if (tag.contains("EnderItems", Tag.TAG_LIST)) {
                    playerEntity.getEnderChestInventory().fromTag(tag.getList("EnderItems", Tag.TAG_COMPOUND));
                }
            } else if (entity instanceof ZombieVillager) {
                if (tag.contains("ConversionTime", 99) && tag.getInt("ConversionTime") > -1) {
                    ((AccessorZombieVillager) entity).invokeStartConverting(tag.hasUUID("ConversionPlayer") ? tag.getUUID("ConversionPlayer") : null, tag.getInt("ConversionTime"));
                }
            }
        }
    }

    // 反序列化 blockEntity 数据
    private static void updateBlockEntityHandler(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        Level world = player.level;
        if (!world.getDimensionLocation().equals(buf.readResourceLocation())) {
            return;
        }
        BlockPos pos = buf.readBlockPos();
        CompoundTag tag = buf.readNbt();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (Configs.saveInventoryToSchematicInServer && pos.equals(PcaSyncUtil.lastUpdatePos)) {
            InfoUtils.showGuiOrInGameMessage(Message.MessageType.SUCCESS, ModInfo.MOD_ID + ".message.loadInventoryToLocalSuccess");
        }
        if (blockEntity != null) {
            ModInfo.LOGGER.debug("update blockEntity!");
            blockEntity.load(tag);
        }
    }

    static public void syncBlockEntity(BlockPos pos) {
        if (lastBlockPos != null && lastBlockPos.equals(pos)) {
            return;
        }
        ModInfo.LOGGER.debug("syncBlockEntity: {}", pos);
        lastBlockPos = pos;
        lastEntityId = -1;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
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
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        ClientPlayNetworking.send(SYNC_ENTITY, buf);
    }

    static public void cancelSyncBlockEntity() {
        if (lastBlockPos == null) {
            return;
        }
        lastBlockPos = null;
        ModInfo.LOGGER.debug("cancelSyncBlockEntity.");
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(CANCEL_SYNC_REQUEST_BLOCK_ENTITY, buf);
    }

    static public void cancelSyncEntity() {
        if (lastEntityId == -1) {
            return;
        }
        lastEntityId = -1;
        ModInfo.LOGGER.debug("cancelSyncEntity.");
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(CANCEL_SYNC_ENTITY, buf);
    }

//    private static class ServerboundIdentifierCustomPayloadListener implements ICustomPayloadListener<ResourceLocation> {
//        @Override
//        public void onCustomPayload(ICustomPayloadEvent<ResourceLocation> event) {
//            ResourceLocation channel = event.getChannel();
//            if (channel.equals(SYNC_BLOCK_ENTITY)) {
//                MultiConnectAPI.instance().forceSendCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
//            } else if (channel.equals(SYNC_ENTITY)) {
//                MultiConnectAPI.instance().forceSendCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
//            } else if (channel.equals(CANCEL_SYNC_REQUEST_BLOCK_ENTITY)) {
//                MultiConnectAPI.instance().forceSendCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
//            } else if (channel.equals(CANCEL_SYNC_ENTITY)) {
//                MultiConnectAPI.instance().forceSendCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
//            }
//        }
//    }
//
//    private static class ClientboundIdentifierCustomPayloadListener implements ICustomPayloadListener<ResourceLocation> {
//        @Override
//        public void onCustomPayload(ICustomPayloadEvent<ResourceLocation> event) {
//            ResourceLocation channel = event.getChannel();
//            if (channel.equals(ENABLE_PCA_SYNC_PROTOCOL)) {
//                enablePcaSyncProtocolHandle(Minecraft.getInstance(), null, event.getData(), null);
//            } else if (channel.equals(DISABLE_PCA_SYNC_PROTOCOL)) {
//                disablePcaSyncProtocolHandle(Minecraft.getInstance(), null, event.getData(), null);
//            } else if (channel.equals(UPDATE_ENTITY)) {
//                updateEntityHandler(Minecraft.getInstance(), null, event.getData(), null);
//            } else if (channel.equals(UPDATE_BLOCK_ENTITY)) {
//                updateBlockEntityHandler(Minecraft.getInstance(), null, event.getData(), null);
//            }
//        }
//    }
}

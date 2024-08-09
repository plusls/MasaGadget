package com.plusls.MasaGadget.util;

import com.mojang.serialization.Dynamic;
import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.api.event.DisconnectListener;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.mixin.accessor.*;
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
import net.minecraft.nbt.NbtOps;
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
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.compat.minecraft.nbt.TagCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.SimpleContainerCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.player.PlayerCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.level.LevelCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.level.block.BlockEntityCompat;
import top.hendrixshen.magiclib.util.minecraft.NetworkUtil;

import java.util.Objects;

//#if MC > 12004
//$$ import com.plusls.MasaGadget.impl.network.packet.*;
//$$ import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//$$ import net.minecraft.Util;
//#endif

public class PcaSyncProtocol {
    private static final String NAMESPACE = "pca";
    // send
    public static final ResourceLocation SYNC_BLOCK_ENTITY = id("sync_block_entity");
    public static final ResourceLocation SYNC_ENTITY = id("sync_entity");
    public static final ResourceLocation CANCEL_SYNC_REQUEST_BLOCK_ENTITY = id("cancel_sync_block_entity");
    public static final ResourceLocation CANCEL_SYNC_ENTITY = id("cancel_sync_entity");
    // recv
    public static final ResourceLocation ENABLE_PCA_SYNC_PROTOCOL = id("enable_pca_sync_protocol");
    public static final ResourceLocation DISABLE_PCA_SYNC_PROTOCOL = id("disable_pca_sync_protocol");
    public static final ResourceLocation UPDATE_ENTITY = id("update_entity");
    public static final ResourceLocation UPDATE_BLOCK_ENTITY = id("update_block_entity");
    public static boolean enable = false;
    private static BlockPos lastBlockPos = null;
    private static int lastEntityId = -1;

    private static @NotNull ResourceLocation id(String path) {
        return ResourceLocationCompat.fromNamespaceAndPath(PcaSyncProtocol.NAMESPACE, path);
    }

    public static void init() {
        //#if MC > 12004
        //$$ PayloadTypeRegistry.playC2S().register(ServerboundCancelSyncBlockEntityPacket.TYPE, ServerboundCancelSyncBlockEntityPacket.CODEC);
        //$$ PayloadTypeRegistry.playC2S().register(ServerboundCancelSyncEntityPacket.TYPE, ServerboundCancelSyncEntityPacket.CODEC);
        //$$ PayloadTypeRegistry.playC2S().register(ServerboundSyncBlockEntityPacket.TYPE, ServerboundSyncBlockEntityPacket.CODEC);
        //$$ PayloadTypeRegistry.playC2S().register(ServerboundSyncEntityPacket.TYPE, ServerboundSyncEntityPacket.CODEC);
        //$$ PayloadTypeRegistry.playS2C().register(ClientboundDisablePcaSyncProtocolPacket.TYPE, ClientboundDisablePcaSyncProtocolPacket.CODEC);
        //$$ PayloadTypeRegistry.playS2C().register(ClientboundEnablePcaSyncProtocolPacket.TYPE, ClientboundEnablePcaSyncProtocolPacket.CODEC);
        //$$ PayloadTypeRegistry.playS2C().register(ClientboundUpdateBlockEntityPacket.TYPE, ClientboundUpdateBlockEntityPacket.CODEC);
        //$$ PayloadTypeRegistry.playS2C().register(ClientboundUpdateEntityPacket.TYPE, ClientboundUpdateEntityPacket.CODEC);
        //$$ ClientPlayNetworking.registerGlobalReceiver(ClientboundDisablePcaSyncProtocolPacket.TYPE, PcaSyncProtocol::disablePcaSyncProtocolHandler);
        //$$ ClientPlayNetworking.registerGlobalReceiver(ClientboundEnablePcaSyncProtocolPacket.TYPE, PcaSyncProtocol::enablePcaSyncProtocolHandler);
        //$$ ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateBlockEntityPacket.TYPE, PcaSyncProtocol::updateBlockEntityHandler);
        //$$ ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateEntityPacket.TYPE, PcaSyncProtocol::updateEntityHandler);
        //#else
        ClientPlayNetworking.registerGlobalReceiver(ENABLE_PCA_SYNC_PROTOCOL, PcaSyncProtocol::enablePcaSyncProtocolHandler);
        ClientPlayNetworking.registerGlobalReceiver(DISABLE_PCA_SYNC_PROTOCOL, PcaSyncProtocol::disablePcaSyncProtocolHandler);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_ENTITY, PcaSyncProtocol::updateEntityHandler);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_BLOCK_ENTITY, PcaSyncProtocol::updateBlockEntityHandler);
        //#endif
        MagicLib.getInstance().getEventManager().register(DisconnectListener.class, PcaSyncProtocol::onDisconnect);
    }

    private static void onDisconnect() {
        SharedConstants.getLogger().info("pcaSyncProtocol onDisconnect.");
        enable = false;
    }

    private static void enablePcaSyncProtocolHandler(
            //#if MC > 12004
            //$$ ClientboundEnablePcaSyncProtocolPacket packet,
            //$$ ClientPlayNetworking.Context context
            //#else
            Minecraft client,
            ClientPacketListener handler,
            FriendlyByteBuf buf,
            PacketSender responseSender
            //#endif
    ) {
        if (!Minecraft.getInstance().hasSingleplayerServer()) {
            SharedConstants.getLogger().info("pcaSyncProtocol enable.");
            enable = true;
        }
    }

    public static void disablePcaSyncProtocolHandler(
            //#if MC > 12004
            //$$ ClientboundDisablePcaSyncProtocolPacket packet,
            //$$ ClientPlayNetworking.Context context
            //#else
            Minecraft client,
            ClientPacketListener handler,
            FriendlyByteBuf buf,
            PacketSender responseSender
            //#endif
    ) {
        if (!Minecraft.getInstance().hasSingleplayerServer()) {
            SharedConstants.getLogger().info("pcaSyncProtocol disable.");
            enable = false;
        }
    }

    // 反序列化实体数据
    public static void updateEntityHandler(
            //#if MC > 12004
            //$$ ClientboundUpdateEntityPacket packet,
            //$$ ClientPlayNetworking.Context context
            //#else
            Minecraft client,
            ClientPacketListener handler,
            FriendlyByteBuf buf,
            PacketSender responseSender
            //#endif
    ) {
        //#if MC > 12004
        //$$ Minecraft client = context.client();
        //#endif
        LocalPlayer player = client.player;

        if (player == null) {
            return;
        }

        PlayerCompat playerCompat = PlayerCompat.of(player);
        LevelCompat levelCompat = playerCompat.getLevelCompat();
        Level level = levelCompat.get();

        if (!levelCompat.getDimensionLocation().equals(
                //#if MC > 12004
                //$$ packet.dimension()
                //#else
                buf.readResourceLocation()
                //#endif
        )) {
            return;
        }

        //#if MC > 12004
        //$$ int entityId = packet.entityId();
        //$$ CompoundTag tag = packet.tag();
        //#else
        int entityId = buf.readInt();
        CompoundTag tag = NetworkUtil.readNbt(buf);
        //#endif
        Entity entity = level.getEntity(entityId);

        if (entity != null) {
            SharedConstants.getLogger().debug("update entity!");
            assert tag != null;

            if (entity instanceof Mob) {
                if (tag.getBoolean("PersistenceRequired")) {
                    ((Mob) entity).setPersistenceRequired();
                }
            }

            if (entity instanceof AbstractMinecartContainer) {
                NonNullList<ItemStack> itemStacks = ((AccessorAbstractMinecartContainer) entity).masa_gadget_mod$getItemStacks();
                itemStacks.clear();
                ContainerHelper.loadAllItems(
                        tag,
                        itemStacks
                        //#if MC > 12004
                        //$$ , client.level.registryAccess()
                        //#endif
                );
            }

            if (entity instanceof AbstractVillager) {
                ((AbstractVillager) entity).getInventory().clearContent();
                SimpleContainerCompat.of(((AbstractVillager) entity).getInventory()).fromTag(
                        tag.getList("Inventory", TagCompat.TAG_COMPOUND)
                        //#if MC > 12004
                        //$$ , client.level.registryAccess()
                        //#endif
                );

                //#if MC > 12004
                //$$ if (tag.contains("Offers")) {
                //$$     MerchantOffers.CODEC
                //$$             .parse(client.level.registryAccess().createSerializationContext(NbtOps.INSTANCE),
                //$$                     tag.get("Offers"))
                //$$             .resultOrPartial(Util.prefix("Failed to load offers: ", MagicLib.getLogger()::warn))
                //$$             .ifPresent(merchantOffers -> ((AccessorAbstractVillager) entity).masa_gadget_mod$setOffers(merchantOffers));
                //$$ }
                //#else
                ((AccessorAbstractVillager) entity).masa_gadget_mod$setOffers(new MerchantOffers(tag.getCompound("Offers")));
                //#endif

                if (entity instanceof Villager) {
                    ((AccessorVillager) entity).masa_gadget_mod$setNumberOfRestocksToday(tag.getInt("RestocksToday"));
                    ((AccessorVillager) entity).masa_gadget_mod$setLastRestockGameTime(tag.getLong("LastRestock"));
                    ((AccessorLivingEntity) entity).masa_gadget_mod$setBrain(((AccessorLivingEntity) entity).masa_gadget_mod$makeBrain(new Dynamic<>(NbtOps.INSTANCE, tag.get("Brain"))));
                }
            }

            if (entity instanceof AbstractHorse) {
                // TODO 写的更优雅一些
                entity.load(tag);
            }

            if (entity instanceof Player) {
                Player playerEntity = (Player) entity;
                PlayerCompat.of(playerEntity).getInventory().load(tag.getList("Inventory", TagCompat.TAG_COMPOUND));

                if (tag.contains("EnderItems", TagCompat.TAG_LIST)) {
                    playerEntity.getEnderChestInventory().fromTag(
                            tag.getList("EnderItems", TagCompat.TAG_COMPOUND)
                            //#if MC > 12004
                            //$$ , client.level.registryAccess()
                            //#endif
                    );
                }
            }

            if (entity instanceof ZombieVillager) {
                if (tag.contains("ConversionTime", 99) && tag.getInt("ConversionTime") > -1) {
                    ((AccessorZombieVillager) entity).masa_gadget_mod$startConverting(tag.hasUUID("ConversionPlayer") ? tag.getUUID("ConversionPlayer") : null, tag.getInt("ConversionTime"));
                }
            }
        }
    }

    // 反序列化 blockEntity 数据
    public static void updateBlockEntityHandler(
            //#if MC > 12004
            //$$ ClientboundUpdateBlockEntityPacket packet,
            //$$ ClientPlayNetworking.Context context
            //#else
            Minecraft client,
            ClientPacketListener handler,
            FriendlyByteBuf buf,
            PacketSender responseSender
            //#endif
    ) {
        //#if MC > 12004
        //$$ Minecraft client = context.client();
        //#endif
        LocalPlayer player = client.player;

        if (player == null) {
            return;
        }

        LevelCompat levelCompat = PlayerCompat.of(player).getLevelCompat();
        Level level = levelCompat.get();

        if (!levelCompat.getDimensionLocation().equals(
                //#if MC > 12004
                //$$ packet.dimension()
                //#else
                buf.readResourceLocation()
                //#endif
        )) {
            return;
        }

        //#if MC > 12004
        //$$ BlockPos pos = packet.blockPos();
        //$$ CompoundTag tag = packet.tag();
        //#else
        BlockPos pos = buf.readBlockPos();
        CompoundTag tag = NetworkUtil.readNbt(buf);
        //#endif
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (Configs.saveInventoryToSchematicInServer.getBooleanValue() && pos.equals(PcaSyncUtil.lastUpdatePos)) {
            InfoUtils.showGuiOrInGameMessage(Message.MessageType.SUCCESS, SharedConstants.getModIdentifier() + ".message.loadInventoryToLocalSuccess");
            PcaSyncUtil.lastUpdatePos = null;
        }

        if (blockEntity != null) {
            SharedConstants.getLogger().debug("update blockEntity!");
            BlockEntityCompat.of(blockEntity).load(
                    Objects.requireNonNull(tag)
                    //#if MC > 12004
                    //$$ , client.level.registryAccess()
                    //#endif
            );
        }
    }

    public static void syncBlockEntity(BlockPos pos) {
        if (lastBlockPos != null && lastBlockPos.equals(pos)) {
            return;
        }

        SharedConstants.getLogger().debug("syncBlockEntity: {}", pos);
        lastBlockPos = pos;
        lastEntityId = -1;
        //#if MC < 12005
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        //#endif
        ClientPlayNetworking.send(
                //#if MC > 12004
                //$$ new ServerboundSyncBlockEntityPacket(pos)
                //#else
                SYNC_BLOCK_ENTITY,
                buf
                //#endif
        );
    }

    public static void syncEntity(int entityId) {
        if (lastEntityId != -1 && lastEntityId == entityId) {
            return;
        }

        SharedConstants.getLogger().debug("syncEntity: {}", entityId);
        lastEntityId = entityId;
        lastBlockPos = null;
        //#if MC < 12005
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        //#endif
        ClientPlayNetworking.send(
                //#if MC > 12004
                //$$ new ServerboundSyncEntityPacket(entityId)
                //#else
                SYNC_ENTITY,
                buf
                //#endif
        );
    }

    public static void cancelSyncBlockEntity() {
        if (lastBlockPos == null) {
            return;
        }

        lastBlockPos = null;
        SharedConstants.getLogger().debug("cancelSyncBlockEntity.");
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(
                //#if MC > 12004
                //$$ new ServerboundCancelSyncBlockEntityPacket()
                //#else
                CANCEL_SYNC_REQUEST_BLOCK_ENTITY,
                buf
                //#endif
        );
    }

    public static void cancelSyncEntity() {
        if (lastEntityId == -1) {
            return;
        }

        lastEntityId = -1;
        SharedConstants.getLogger().debug("cancelSyncEntity.");
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(
                //#if MC > 12004
                //$$ new ServerboundCancelSyncEntityPacket()
                //#else
                CANCEL_SYNC_ENTITY,
                buf
                //#endif
        );
    }
}

package com.plusls.MasaGadget.util;

import com.mojang.serialization.Dynamic;
import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.api.event.DisconnectListener;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.mixin.accessor.AccessorAbstractMinecartContainer;
import com.plusls.MasaGadget.mixin.accessor.AccessorAbstractVillager;
import com.plusls.MasaGadget.mixin.accessor.AccessorLivingEntity;
import com.plusls.MasaGadget.mixin.accessor.AccessorVillager;
import com.plusls.MasaGadget.mixin.accessor.AccessorZombieVillager;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
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
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.SimpleContainerCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.player.PlayerCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.level.LevelCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.level.block.BlockEntityCompat;
import top.hendrixshen.magiclib.api.network.packet.ClientboundPacketHandler;
import top.hendrixshen.magiclib.api.network.packet.MagicPackets;
import top.hendrixshen.magiclib.api.network.packet.PacketCodec;
import top.hendrixshen.magiclib.api.network.packet.PacketType;
import top.hendrixshen.magiclib.api.network.packet.ServerboundPacketHandler;
import top.hendrixshen.magiclib.util.minecraft.NetworkUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//#if MC >= 12106
//$$ import com.mojang.logging.LogUtils;
//$$ import net.minecraft.util.ProblemReporter;
//$$ import net.minecraft.world.ItemStackWithSlot;
//$$ import net.minecraft.world.level.storage.TagValueInput;
//$$ import net.minecraft.world.level.storage.ValueInput;
//$$ import org.slf4j.Logger;
//#else
import top.hendrixshen.magiclib.api.compat.minecraft.nbt.TagCompat;
//#endif

//#if MC > 12104
//$$ import net.minecraft.core.UUIDUtil;
//#endif

//#if MC > 12004
//$$ import net.minecraft.Util;
//#endif

public class PcaSyncProtocol {
    //#if MC >= 12106
    //$$ private static final Logger LOGGER = LogUtils.getLogger();
    //#endif

    private static final String NAMESPACE = "pca";
    private static final AtomicBoolean registeredPackers = new AtomicBoolean();
    private static BlockPos lastBlockPos = null;
    private static int lastEntityId = -1;

    // Serverbound
    public static final PacketType<FriendlyByteBuf> SYNC_BLOCK_ENTITY = PcaSyncProtocol.id("sync_block_entity");
    public static final PacketType<FriendlyByteBuf> SYNC_ENTITY = PcaSyncProtocol.id("sync_entity");
    public static final PacketType<FriendlyByteBuf> CANCEL_SYNC_REQUEST_BLOCK_ENTITY = PcaSyncProtocol.id("cancel_sync_block_entity");
    public static final PacketType<FriendlyByteBuf> CANCEL_SYNC_ENTITY = PcaSyncProtocol.id("cancel_sync_entity");
    // Clientbound
    public static final PacketType<FriendlyByteBuf> ENABLE_PCA_SYNC_PROTOCOL = PcaSyncProtocol.id("enable_pca_sync_protocol");
    public static final PacketType<FriendlyByteBuf> DISABLE_PCA_SYNC_PROTOCOL = PcaSyncProtocol.id("disable_pca_sync_protocol");
    public static final PacketType<FriendlyByteBuf> UPDATE_ENTITY = PcaSyncProtocol.id("update_entity");
    public static final PacketType<FriendlyByteBuf> UPDATE_BLOCK_ENTITY = PcaSyncProtocol.id("update_block_entity");
    public static boolean enable = false;

    private static @NotNull PacketType<FriendlyByteBuf> id(String path) {
        return PacketType.of(ResourceLocationCompat.fromNamespaceAndPath(PcaSyncProtocol.NAMESPACE, path));
    }

    public static void registerPackets() {
        if (!PcaSyncProtocol.registeredPackers.compareAndSet(false, true)) {
            return;
        }

        PacketCodec<FriendlyByteBuf> codec = PacketCodec.of(
                (p, buf) -> buf.writeBytes(p.copy()),
                buf -> {
                    FriendlyByteBuf p = new FriendlyByteBuf(Unpooled.buffer());
                    p.writeBytes(buf);
                    return p;
                }
        );

        Consumer<PacketType<FriendlyByteBuf>> serverbound = type ->
                MagicPackets.registerServerbound(type, codec, ServerboundPacketHandler.dummy());

        BiConsumer<PacketType<FriendlyByteBuf>, ClientboundPacketHandler<FriendlyByteBuf>> clientbound = (type, handler) ->
                MagicPackets.registerClientbound(type, codec, handler);

        serverbound.accept(PcaSyncProtocol.SYNC_BLOCK_ENTITY);
        serverbound.accept(PcaSyncProtocol.SYNC_ENTITY);
        serverbound.accept(PcaSyncProtocol.CANCEL_SYNC_REQUEST_BLOCK_ENTITY);
        serverbound.accept(PcaSyncProtocol.CANCEL_SYNC_ENTITY);
        clientbound.accept(PcaSyncProtocol.ENABLE_PCA_SYNC_PROTOCOL, PcaSyncProtocol::enablePcaSyncProtocolHandler);
        clientbound.accept(PcaSyncProtocol.DISABLE_PCA_SYNC_PROTOCOL, PcaSyncProtocol::disablePcaSyncProtocolHandler);
        clientbound.accept(PcaSyncProtocol.UPDATE_ENTITY, PcaSyncProtocol::updateEntityHandler);
        clientbound.accept(PcaSyncProtocol.UPDATE_BLOCK_ENTITY, PcaSyncProtocol::updateBlockEntityHandler);
    }

    public static void init() {
        PcaSyncProtocol.registerPackets();
        MagicLib.getInstance().getEventManager().register(DisconnectListener.class, PcaSyncProtocol::onDisconnect);
    }

    private static void onDisconnect() {
        SharedConstants.getLogger().info("pcaSyncProtocol onDisconnect.");
        enable = false;
    }

    private static void enablePcaSyncProtocolHandler(FriendlyByteBuf buf, ClientboundPacketHandler.Context context) {
        if (!Minecraft.getInstance().hasSingleplayerServer()) {
            SharedConstants.getLogger().info("pcaSyncProtocol enable.");
            enable = true;
        }
    }

    private static void disablePcaSyncProtocolHandler(FriendlyByteBuf buf, ClientboundPacketHandler.Context context) {
        if (!Minecraft.getInstance().hasSingleplayerServer()) {
            SharedConstants.getLogger().info("pcaSyncProtocol disable.");
            enable = false;
        }
    }

    private static void updateEntityHandler(FriendlyByteBuf buf, ClientboundPacketHandler.Context context) {
        Minecraft mc = context.getClient();
        LocalPlayer player = mc.player;

        if (player == null) {
            return;
        }

        PlayerCompat playerCompat = PlayerCompat.of(player);
        LevelCompat levelCompat = playerCompat.getLevelCompat();
        Level level = levelCompat.get();

        if (!levelCompat.getDimensionLocation().equals(buf.readResourceLocation())) {
            return;
        }

        int entityId = buf.readInt();
        CompoundTag tag = NetworkUtil.readNbtAuto(buf);
        Entity entity = level.getEntity(entityId);

        if (entity != null) {
            SharedConstants.getLogger().debug("update entity!");

            //#if MC >= 12106
            //$$ ValueInput input;
            //$$
            //$$ try (ProblemReporter.ScopedCollector collector = new ProblemReporter.ScopedCollector(entity.problemPath(), PcaSyncProtocol.LOGGER)) {
            //$$     input = TagValueInput.create(collector, entity.registryAccess(), tag);
            //$$ }
            //#endif

            if (entity instanceof Mob) {
                if (
                    //#if MC > 12104
                    //$$ tag.getBoolean("PersistenceRequired").orElse(false)
                    //#else
                        tag.getBoolean("PersistenceRequired")
                    //#endif
                ) {
                    ((Mob) entity).setPersistenceRequired();
                }
            }

            if (entity instanceof AbstractMinecartContainer) {
                NonNullList<ItemStack> itemStacks = ((AccessorAbstractMinecartContainer) entity).masa_gadget_mod$getItemStacks();
                itemStacks.clear();
                ContainerHelper.loadAllItems(
                        //#if MC >= 12106
                        //$$ input,
                        //#else
                        tag,
                        //#endif
                        itemStacks
                        //#if 12106 >= MC && MC > 12004
                        //$$ , mc.level.registryAccess()
                        //#endif
                );
            }

            if (entity instanceof AbstractVillager) {
                ((AbstractVillager) entity).getInventory().clearContent();
                //#if MC >= 12106
                //$$ input.list("Inventory", ItemStack.CODEC).ifPresent(itemStacks ->
                //$$         SimpleContainerCompat.of(((AbstractVillager) entity).getInventory()).fromTag(itemStacks));
                //$$ ((AccessorAbstractVillager) entity).masa_gadget_mod$setOffers(input.read("Offers", MerchantOffers.CODEC).orElse(null));
                //#else
                SimpleContainerCompat.of(((AbstractVillager) entity).getInventory()).fromTag(
                        //#if MC > 12104
                        //$$ tag.getListOrEmpty("Inventory")
                        //#else
                        tag.getList("Inventory", TagCompat.TAG_COMPOUND)
                        //#endif
                        //#if MC > 12004
                        //$$ , mc.level.registryAccess()
                        //#endif
                );

                //#if MC > 12004
                //$$ if (tag.contains("Offers")) {
                //$$     MerchantOffers.CODEC
                //$$             .parse(mc.level.registryAccess().createSerializationContext(NbtOps.INSTANCE),
                //$$                     tag.get("Offers"))
                //$$             .resultOrPartial(Util.prefix("Failed to load offers: ", MagicLib.getLogger()::warn))
                //$$             .ifPresent(merchantOffers -> ((AccessorAbstractVillager) entity).masa_gadget_mod$setOffers(merchantOffers));
                //$$ }
                //#else
                ((AccessorAbstractVillager) entity).masa_gadget_mod$setOffers(new MerchantOffers(tag.getCompound("Offers")));
                //#endif
                //#endif

                if (entity instanceof Villager) {
                    ((AccessorVillager) entity).masa_gadget_mod$setNumberOfRestocksToday(
                            //#if MC > 12104
                            //$$ tag.getIntOr("RestocksToday", 0)
                            //#else
                            tag.getInt("RestocksToday")
                            //#endif
                    );
                    ((AccessorVillager) entity).masa_gadget_mod$setLastRestockGameTime(
                            //#if MC > 12104
                            //$$ tag.getLongOr("RestocksToday", 0L)
                            //#else
                            tag.getLong("LastRestock")
                            //#endif
                    );
                    ((AccessorLivingEntity) entity).masa_gadget_mod$setBrain(((AccessorLivingEntity) entity).masa_gadget_mod$makeBrain(new Dynamic<>(NbtOps.INSTANCE, tag.get("Brain"))));
                }
            }

            if (entity instanceof AbstractHorse) {
                // TODO 写的更优雅一些
                entity.load(
                        //#if MC >= 12106
                        //$$ input
                        //#else
                        tag
                        //#endif
                );
            }

            if (entity instanceof Player) {
                Player playerEntity = (Player) entity;
                //#if MC >= 12106
                //$$ PlayerCompat.of(playerEntity).getInventory().load(input.listOrEmpty("Inventory", ItemStackWithSlot.CODEC));
                //$$ playerEntity.getEnderChestInventory().fromSlots(input.listOrEmpty("EnderItems", ItemStackWithSlot.CODEC));
                //#else
                PlayerCompat.of(playerEntity).getInventory().load(
                        //#if MC > 12104
                        //$$ tag.getListOrEmpty("Inventory")
                        //#else
                        tag.getList("Inventory", TagCompat.TAG_COMPOUND)
                        //#endif
                );

                //#if MC > 12104
                //$$ tag.getList("EnderItems").ifPresent(tags ->
                //$$         playerEntity.getEnderChestInventory().fromTag(tags, mc.level.registryAccess())
                //$$ );
                //#else
                if (tag.contains("EnderItems", TagCompat.TAG_LIST)) {
                    playerEntity.getEnderChestInventory().fromTag(
                            tag.getList("EnderItems", TagCompat.TAG_COMPOUND)
                            //#if MC > 12004
                            //$$ , mc.level.registryAccess()
                            //#endif
                    );
                }
                //#endif
                //#endif
            }

            if (entity instanceof ZombieVillager) {
                //#if MC > 12104
                //$$ int conversionTime = tag.getIntOr("ConversionTime", -1);
                //$$
                //$$ if (conversionTime > -1) {
                //$$     tag.read("ConversionPlayer", UUIDUtil.CODEC).ifPresent(uuid ->
                //$$         ((AccessorZombieVillager) entity).masa_gadget_mod$startConverting(uuid, conversionTime)
                //$$     );
                //$$ }
                //#else
                if (tag.contains("ConversionTime", 99) && tag.getInt("ConversionTime") > -1) {
                    ((AccessorZombieVillager) entity).masa_gadget_mod$startConverting(tag.hasUUID("ConversionPlayer") ? tag.getUUID("ConversionPlayer") : null, tag.getInt("ConversionTime"));
                }
                //#endif
            }
        }
    }

    private static void updateBlockEntityHandler(FriendlyByteBuf buf, ClientboundPacketHandler.Context context) {
        Minecraft mc = context.getClient();
        LocalPlayer player = mc.player;

        if (player == null) {
            return;
        }

        LevelCompat levelCompat = PlayerCompat.of(player).getLevelCompat();
        Level level = levelCompat.get();

        if (!levelCompat.getDimensionLocation().equals(buf.readResourceLocation())) {
            return;
        }

        BlockPos pos = buf.readBlockPos();
        CompoundTag tag = NetworkUtil.readNbtAuto(buf);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (Configs.saveInventoryToSchematicInServer.getBooleanValue() && pos.equals(PcaSyncUtil.lastUpdatePos)) {
            InfoUtils.showGuiOrInGameMessage(Message.MessageType.SUCCESS, SharedConstants.tr("message.loadInventoryToLocalSuccess"));
            PcaSyncUtil.lastUpdatePos = null;
        }

        if (blockEntity != null) {
            SharedConstants.getLogger().debug("update blockEntity!");
            BlockEntityCompat.of(blockEntity).load(
                    Objects.requireNonNull(tag)
                    //#if MC > 12004
                    //$$ , mc.level.registryAccess()
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
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        MagicPackets.sendServerbound(PcaSyncProtocol.SYNC_BLOCK_ENTITY, buf);
    }

    public static void syncEntity(int entityId) {
        if (lastEntityId != -1 && lastEntityId == entityId) {
            return;
        }

        SharedConstants.getLogger().debug("syncEntity: {}", entityId);
        lastEntityId = entityId;
        lastBlockPos = null;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        MagicPackets.sendServerbound(PcaSyncProtocol.SYNC_ENTITY, buf);
    }

    public static void cancelSyncBlockEntity() {
        if (lastBlockPos == null) {
            return;
        }

        lastBlockPos = null;
        SharedConstants.getLogger().debug("cancelSyncBlockEntity.");
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        MagicPackets.sendServerbound(PcaSyncProtocol.CANCEL_SYNC_REQUEST_BLOCK_ENTITY, buf);
    }

    public static void cancelSyncEntity() {
        if (lastEntityId == -1) {
            return;
        }

        lastEntityId = -1;
        SharedConstants.getLogger().debug("cancelSyncEntity.");
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        MagicPackets.sendServerbound(PcaSyncProtocol.CANCEL_SYNC_ENTITY, buf);
    }
}

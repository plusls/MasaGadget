package io.github.plusls.MasaGadget.network;

import fi.dy.masa.minihud.util.DataStorage;
import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.github.plusls.MasaGadget.util.ParseBborPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// public class ClientNetworkHandler implements IIdentifierCustomPayloadListener {
public class ClientNetworkHandler {

    public static final Identifier BBOR_INITIALIZE = new Identifier("bbor:initialize");
    public static final Identifier BBOR_ADD_BOUNDING_BOX_V2 = new Identifier("bbor:add_bounding_box_v2");
    public static final Identifier BBOR_SUBSCRIBE = new Identifier("bbor:subscribe");

    private static final Identifier ENABLE_PCA_SYNC_PROTOCOL = new Identifier("plusls_carpet_addition_mod:enable_pca_sync_protocol");
    private static final Identifier DISABLE_PCA_SYNC_PROTOCOL = new Identifier("plusls_carpet_addition_mod:disable_pca_sync_protocol");
    private static final Identifier UPDATE_ENTITY = new Identifier("plusls_carpet_addition_mod:update_entity");
    private static final Identifier UPDATE_BLOCK_ENTITY = new Identifier("plusls_carpet_addition_mod:update_block_entity");

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ENABLE_PCA_SYNC_PROTOCOL, ClientNetworkHandler::enablePcaSyncProtocolHandle);
        ClientPlayNetworking.registerGlobalReceiver(DISABLE_PCA_SYNC_PROTOCOL, ClientNetworkHandler::disablePcaSyncProtocolHandle);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_ENTITY, ClientNetworkHandler::updateEntityHandler);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_BLOCK_ENTITY, ClientNetworkHandler::updateBlockEntityHandler);
    }

    private static void enablePcaSyncProtocolHandle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (!client.isIntegratedServerRunning()) {
            MasaGadgetMod.LOGGER.info("pcaSyncProtocol enable.");
            MasaGadgetMod.pcaSyncProtocol = true;
        }
    }

    private static void disablePcaSyncProtocolHandle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (!client.isIntegratedServerRunning()) {
            MasaGadgetMod.LOGGER.info("pcaSyncProtocol disable.");
            MasaGadgetMod.pcaSyncProtocol = false;
        }
    }


    // 反序列化实体数据
    private static void updateEntityHandler(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        World world = player.world;
        if (!world.getRegistryKey().getValue().equals(buf.readIdentifier())) {
            return;
        }
        int entityId = buf.readInt();
        CompoundTag tag = buf.readCompoundTag();
        Entity entity = world.getEntityById(entityId);
        if (entity != null) {
            MasaGadgetMod.LOGGER.debug("update entity!");
            entity.fromTag(tag);
        }
    }

    // 反序列化 blockEntity 数据
    private static void updateBlockEntityHandler(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        World world = player.world;
        if (!world.getRegistryKey().getValue().equals(buf.readIdentifier())) {
            return;
        }
        BlockPos pos = buf.readBlockPos();
        CompoundTag tag = buf.readCompoundTag();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            MasaGadgetMod.LOGGER.debug("update blockEntity!");
            blockEntity.fromTag(world.getBlockState(pos), tag);
        }
    }


    public static void bborInitializeHandler(PacketByteBuf data) {
        long seed = data.readLong();
        int spawnX = data.readInt();
        int spawnZ = data.readInt();
        ParseBborPacket.seedCache = seed;
        ParseBborPacket.spawnPos = new BlockPos(spawnX, 0, spawnZ);
        ParseBborPacket.structuresCache = new ListTag();
        if (!ParseBborPacket.carpetOrservux) {
            ParseBborPacket.enable = true;
            DataStorage.getInstance().setWorldSeed(ParseBborPacket.seedCache);
            DataStorage.getInstance().setWorldSpawn(ParseBborPacket.spawnPos);
            MasaGadgetMod.LOGGER.info("init seed: {}", ParseBborPacket.seedCache);
        }
        if (!MasaGadgetMod.bborCompat) {
            ClientPlayNetworking.send(BBOR_SUBSCRIBE, new PacketByteBuf(Unpooled.buffer()));
        }
    }

    public static void bborAddBoundingBoxV2Handler(PacketByteBuf data) {
        ParseBborPacket.parse(data);
    }

//    @Override
//    public void onCustomPayload(int protocol, Identifier channel, PacketByteBuf data) {
//        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
//        PacketContext context = null;
//        if (handler instanceof PacketContext) {
//            context = (PacketContext) (Object) handler;
//        } else {
//            return;
//        }
//        if (channel.equals(ClientNetworkHandler.HELLO)) {
//            ClientNetworkHandler.helloHandler(context, data);
//        } else if (channel.equals(ClientNetworkHandler.RESPONSE_ENTITY)) {
//            ClientNetworkHandler.responseEntityHandler(context, data);
//        } else if (channel.equals(new Identifier(BBOR_INITIALIZE.toString()))) {
//            ClientNetworkHandler.bborInitializeHandler(context, data);
//        } else if (channel.equals(new Identifier(BBOR_ADD_BOUNDING_BOX_V2.toString()))) {
//            ClientNetworkHandler.bborAddBoundingBoxV2Handler(context, data);
//        }
//    }
}

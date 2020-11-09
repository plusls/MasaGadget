package io.github.plusls.MasaGadget.network;

import fi.dy.masa.minihud.util.DataStorage;
import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.github.plusls.MasaGadget.util.ParseBborPacket;
import io.netty.buffer.Unpooled;
import net.earthcomputer.multiconnect.api.IIdentifierCustomPayloadListener;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClientNetworkHandler implements IIdentifierCustomPayloadListener {
    public static final Identifier HELLO = MasaGadgetMod.id("hello");
    public static final Identifier RESPONSE_ENTITY = MasaGadgetMod.id("response_entity");
    public static final Identifier BBOR_INITIALIZE = new Identifier("bbor:initialize");
    public static final Identifier BBOR_ADD_BOUNDING_BOX_V2 = new Identifier("bbor:add_bounding_box_v2");

    public static void init() {
        ClientSidePacketRegistry.INSTANCE.register(HELLO, ClientNetworkHandler::helloHandler);
        ClientSidePacketRegistry.INSTANCE.register(RESPONSE_ENTITY, ClientNetworkHandler::responseEntityHandler);
    }

    private static void helloHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        if (!MinecraftClient.getInstance().isIntegratedServerRunning()) {
            MasaGadgetMod.LOGGER.info("MasaGadget detected.");
            MasaGadgetMod.masaGagdetInServer = true;
        }
    }

    // 反序列化实体数据
    private static void responseEntityHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        PlayerEntity player = packetContext.getPlayer();
        World world = player.world;
        int entityId = packetByteBuf.readInt();
        CompoundTag tag = packetByteBuf.readCompoundTag();
        Entity entity = world.getEntityById(entityId);
        if (entity != null) {
            MasaGadgetMod.LOGGER.debug("update entity!");
            entity.fromTag(tag);
        }
    }

    public static void bborInitializeHandler(PacketContext packetContext, PacketByteBuf data) {
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
            ClientSidePacketRegistry.INSTANCE.sendToServer(new Identifier("bbor", "subscribe"),
                    new PacketByteBuf(Unpooled.buffer()));
        }
    }

    public static void bborAddBoundingBoxV2Handler(PacketContext packetContext, PacketByteBuf data) {
        ParseBborPacket.parse(data);
    }

    @Override
    public void onCustomPayload(int protocol, Identifier channel, PacketByteBuf data) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        PacketContext context = null;
        if (handler instanceof PacketContext) {
            context = (PacketContext) (Object) handler;
        } else {
            return;
        }
        if (channel.equals(ClientNetworkHandler.HELLO)) {
            ClientNetworkHandler.helloHandler(context, data);
        } else if (channel.equals(ClientNetworkHandler.RESPONSE_ENTITY)) {
            ClientNetworkHandler.responseEntityHandler(context, data);
        } else if (channel.equals(new Identifier(BBOR_INITIALIZE.toString()))) {
            ClientNetworkHandler.bborInitializeHandler(context, data);
        } else if (channel.equals(new Identifier(BBOR_ADD_BOUNDING_BOX_V2.toString()))) {
            ClientNetworkHandler.bborAddBoundingBoxV2Handler(context, data);
        }
    }
}

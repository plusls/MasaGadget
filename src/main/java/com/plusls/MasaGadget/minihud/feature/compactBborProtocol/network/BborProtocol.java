package com.plusls.MasaGadget.minihud.feature.compactBborProtocol.network;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.MasaGadgetMod;
import com.plusls.MasaGadget.util.DisconnectEvent;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.minihud.util.StructureType;
import io.netty.buffer.Unpooled;
import net.earthcomputer.multiconnect.api.ICustomPayloadEvent;
import net.earthcomputer.multiconnect.api.ICustomPayloadListener;
import net.earthcomputer.multiconnect.api.MultiConnectAPI;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BborProtocol {
    private static final String NAMESPACE = "bbor";

    private static Identifier id(String path) {
        return new Identifier(NAMESPACE, path);
    }

    // recv
    private static final Identifier INITIALIZE = id("initialize");
    private static final Identifier ADD_BOUNDING_BOX_V2 = id("add_bounding_box_v2");

    // send
    private static final Identifier SUBSCRIBE = id("subscribe");

    private static final HashMap<Integer, String> BBOR_ID_TO_MINIHUD_ID = new HashMap<>();
    public static NbtList structuresCache = null;
    public static Long seedCache = null;
    public static BlockPos spawnPos = null;
    public static boolean enable = false;
    public static boolean carpetOrServux = false;
    public static final ReentrantLock lock = new ReentrantLock(true);

    private static final ClientboundIdentifierCustomPayloadListener clientboundIdentifierCustomPayloadListener =
            new ClientboundIdentifierCustomPayloadListener();
    private static final ServerboundIdentifierCustomPayloadListener serverboundIdentifierCustomPayloadListener =
            new ServerboundIdentifierCustomPayloadListener();

    static {
        for (StructureType type : StructureType.VALUES) {
            String structureName = type.getStructureName();
            if (type.getFeature() != null) {
                Identifier key = Registry.STRUCTURE_FEATURE.getId(type.getFeature());
                if (key != null) {
                    BBOR_ID_TO_MINIHUD_ID.put(structureName.hashCode(), key.toString());
                    BBOR_ID_TO_MINIHUD_ID.put(lowVersionStructureName(structureName).hashCode(), key.toString());

                }
            }
        }
    }

    public static String lowVersionStructureName(String name) {
        String splitResult[] = name.split("_");
        for (int i = 0; i < splitResult.length; ++i) {
            splitResult[i] = splitResult[i].substring(0, 1).toUpperCase() + splitResult[i].substring(1);
        }
        String ret = String.join("_",splitResult);;
        return ret;
    }

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register(BborProtocol::onJoinServer);
        // fabric-api 的实现有 bug 该事件仅会响应服务端主动断开连接的情况
        // ClientPlayConnectionEvents.DISCONNECT.register(BborProtocol::onDisconnect);
        DisconnectEvent.register(BborProtocol::onDisconnect);
        MultiConnectAPI.instance().addClientboundIdentifierCustomPayloadListener(clientboundIdentifierCustomPayloadListener);
        MultiConnectAPI.instance().addServerboundIdentifierCustomPayloadListener(serverboundIdentifierCustomPayloadListener);
    }

    private static class ServerboundIdentifierCustomPayloadListener implements ICustomPayloadListener<Identifier> {
        @Override
        public void onCustomPayload(ICustomPayloadEvent<Identifier> event) {
            Identifier channel = event.getChannel();
            if (channel.equals(SUBSCRIBE)) {
                MasaGadgetMod.LOGGER.debug("Multiconnect send bbor:SUBSCRIBE");
                MultiConnectAPI.instance().forceSendCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
            }
        }
    }

    private static class ClientboundIdentifierCustomPayloadListener implements ICustomPayloadListener<Identifier> {
        @Override
        public void onCustomPayload(ICustomPayloadEvent<Identifier> event) {
            Identifier channel = event.getChannel();
            if (channel.equals(ADD_BOUNDING_BOX_V2)) {
                MasaGadgetMod.LOGGER.debug("Multiconnect recv bbor:ADD_BOUNDING_BOX_V2");
                bborAddBoundingBoxV2Handler(event.getData());
            }
        }
    }


    private static void onDisconnect() {
        MasaGadgetMod.LOGGER.info("BborProtocol onDisconnect");
        BborProtocol.seedCache = null;
        BborProtocol.spawnPos = null;
        BborProtocol.structuresCache = null;
        BborProtocol.enable = false;
        BborProtocol.carpetOrServux = false;
        // 为了鲁棒性考虑 断开连接时应该确保当前的锁已解开
        while (BborProtocol.lock.isLocked()) {
            BborProtocol.lock.unlock();
        }
    }

    private static void onJoinServer(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (!MasaGadgetMixinPlugin.isBborLoaded) {
            MasaGadgetMod.LOGGER.debug("SUBSCRIBE BBOR.");
            sender.sendPacket(SUBSCRIBE, new PacketByteBuf(Unpooled.buffer()));
        }
    }

    public static void bborProtocolHandler(Identifier channel, PacketByteBuf data) {
        if (channel.equals(INITIALIZE)) {
            bborInitializeHandler(data);
        } else if (channel.equals(ADD_BOUNDING_BOX_V2)) {
            bborAddBoundingBoxV2Handler(data);
        }
    }

    private static void bborInitializeHandler(PacketByteBuf data) {
        long seed = data.readLong();
        int spawnX = data.readInt();
        int spawnZ = data.readInt();
        BborProtocol.seedCache = seed;
        BborProtocol.spawnPos = new BlockPos(spawnX, 0, spawnZ);
        BborProtocol.structuresCache = new NbtList();
        // 若是未加载 MiniHUD，则不会去 mixin CustomPayloadS2CPacket，因此不会有机会调用该函数
        // 因此无需对是否加载 MiniHUD 进行特判
        if (!BborProtocol.carpetOrServux) {
            BborProtocol.enable = true;
            DataStorage.getInstance().setWorldSeed(BborProtocol.seedCache);
            DataStorage.getInstance().setWorldSpawn(BborProtocol.spawnPos);
            MasaGadgetMod.LOGGER.info("init seed: {}", BborProtocol.seedCache);
        }
    }

    private static void bborAddBoundingBoxV2Handler(PacketByteBuf data) {
        BborProtocol.parse(data);
    }

    static public String bborIdToMinihudId(int bborId) {
        return BBOR_ID_TO_MINIHUD_ID.getOrDefault(bborId, "");
    }

    static public void parse(PacketByteBuf buf) {
        Identifier dimensionId = buf.readIdentifier();
        MasaGadgetMod.LOGGER.debug("dimensionId = {}", dimensionId.toString());

        NbtCompound tag = BoundingBoxDeserializer.deserializeStructure(buf);

        if (tag != null) {
            NbtList structures = new NbtList();
            structures.add(tag);
            structuresCache.add(tag);
            if (enable) {
                BborProtocol.lock.lock();
                DataStorage.getInstance().addOrUpdateStructuresFromServer(structures, 0x7fffffff - 0x1000, false);
                BborProtocol.lock.unlock();
            }
        }
    }
}

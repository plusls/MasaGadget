package com.plusls.MasaGadget.minihud.compactBborProtocol;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.DisconnectEvent;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.minihud.util.StructureType;
import io.netty.buffer.Unpooled;
import net.earthcomputer.multiconnect.api.MultiConnectAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BborProtocol {
    public static final String NAMESPACE = "bbor";

    private static Identifier id(String path) {
        return new Identifier(NAMESPACE, path);
    }

    // recv
    private static final Identifier INITIALIZE = id("initialize");
    private static final Identifier ADD_BOUNDING_BOX_V2 = id("add_bounding_box_v2");

    // send
    private static final Identifier SUBSCRIBE = id("subscribe");

    private static final HashMap<Integer, String> BBOR_ID_TO_MINIHUD_ID = new HashMap<>();
    public static Map<Identifier, NbtList> structuresCache = null;
    public static Long seedCache = null;
    public static BlockPos spawnPos = null;
    public static boolean subscribed = false;
    public static boolean enable = false;
    public static boolean carpetOrServux = false;
    public static final ReentrantLock lock = new ReentrantLock(true);

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
        String[] splitResult = name.split("_");
        for (int i = 0; i < splitResult.length; ++i) {
            splitResult[i] = splitResult[i].substring(0, 1).toUpperCase() + splitResult[i].substring(1);
        }
        return String.join("_", splitResult);
    }

    public static void init() {
        // fabric-api 的实现有 bug 该事件仅会响应服务端主动断开连接的情况
        // ClientPlayConnectionEvents.DISCONNECT.register(BborProtocol::onDisconnect);
        DisconnectEvent.register(BborProtocol::onDisconnect);
        MultiConnectAPI.instance().addClientboundIdentifierCustomPayloadListener(event -> {
            Identifier channel = event.getChannel();
            bborProtocolHandler(event.getNetworkHandler(), channel, event.getData());
        });
        MultiConnectAPI.instance().addServerboundIdentifierCustomPayloadListener(event -> {
            Identifier channel = event.getChannel();
            if (channel.equals(SUBSCRIBE)) {
                ModInfo.LOGGER.debug("Multiconnect send bbor:SUBSCRIBE");
                MultiConnectAPI.instance().forceSendCustomPayload(event.getNetworkHandler(), event.getChannel(), event.getData());
            }
        });
    }


    private static void onDisconnect() {
        ModInfo.LOGGER.info("BborProtocol onDisconnect");
        BborProtocol.seedCache = null;
        BborProtocol.spawnPos = null;
        BborProtocol.structuresCache = null;
        BborProtocol.subscribed = false;
        BborProtocol.enable = false;
        BborProtocol.carpetOrServux = false;
        // 为了鲁棒性考虑 断开连接时应该确保当前的锁已解开
        while (BborProtocol.lock.isLocked()) {
            BborProtocol.lock.unlock();
        }
    }

    private static void subscribeBbor(ClientPlayNetworkHandler clientPlayNetworkHandler) {
        if (!MasaGadgetMixinPlugin.isBborLoaded && !BborProtocol.subscribed) {
            BborProtocol.subscribed = true;
            ModInfo.LOGGER.debug("SUBSCRIBE BBOR.");
            clientPlayNetworkHandler.sendPacket(new CustomPayloadC2SPacket(SUBSCRIBE, new PacketByteBuf(Unpooled.buffer())));
        }
    }

    public static void bborProtocolHandler(ClientPlayNetworkHandler clientPlayNetworkHandler, Identifier channel, PacketByteBuf data) {
        try {
            if (channel.equals(INITIALIZE)) {
                bborInitializeHandler(clientPlayNetworkHandler, data);
            } else if (channel.equals(ADD_BOUNDING_BOX_V2)) {
                bborAddBoundingBoxV2Handler(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void bborInitializeHandler(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf data) {
        long seed = data.readLong();
        int spawnX = data.readInt();
        int spawnZ = data.readInt();
        BborProtocol.seedCache = seed;
        BborProtocol.spawnPos = new BlockPos(spawnX, 0, spawnZ);
        BborProtocol.structuresCache = new ConcurrentHashMap<>();
        // 若是未加载 MiniHUD，则不会去 mixin CustomPayloadS2CPacket，因此不会有机会调用该函数
        // 因此无需对是否加载 MiniHUD 进行特判
        if (!BborProtocol.carpetOrServux) {
            BborProtocol.enable = true;
            initMetaData();
            subscribeBbor(clientPlayNetworkHandler);
            ModInfo.LOGGER.info("init seed: {}", BborProtocol.seedCache);
        }
    }

    public static void bborInit(Identifier dimensionId) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        subscribeBbor(player.networkHandler);
        initMetaData();
        bborRefreshData(dimensionId);
    }

    public static void initMetaData() {
        if (BborProtocol.seedCache != null) {
            DataStorage.getInstance().setWorldSeed(BborProtocol.seedCache);
        }
        if (BborProtocol.spawnPos != null) {
            DataStorage.getInstance().setWorldSpawn(BborProtocol.spawnPos);
        }
    }

    public static void bborRefreshData(Identifier dimensionId) {
        if (!structuresCache.containsKey(dimensionId)) {
            structuresCache.put(dimensionId, new NbtList());
        }
        if (BborProtocol.structuresCache != null) {
            BborProtocol.lock.lock();
            DataStorage.getInstance().addOrUpdateStructuresFromServer(BborProtocol.structuresCache.get(dimensionId), 0x7fffffff - 0x1000, false);
            BborProtocol.lock.unlock();
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
        ModInfo.LOGGER.debug("dimensionId = {}", dimensionId.toString());

        NbtCompound tag = BoundingBoxDeserializer.deserializeStructure(buf);
        if (!structuresCache.containsKey(dimensionId)) {
            structuresCache.put(dimensionId, new NbtList());
        }
        if (tag != null) {
            structuresCache.get(dimensionId).add(tag);
            BborProtocol.lock.lock();
            if (enable && Configs.Minihud.COMPACT_BBOR_PROTOCOL.getBooleanValue() && MinecraftClient.getInstance().world != null) {
                DataStorage.getInstance().addOrUpdateStructuresFromServer(structuresCache.get(dimensionId), 0x7fffffff - 0x1000, false);
            }
            BborProtocol.lock.unlock();
        }
    }
}

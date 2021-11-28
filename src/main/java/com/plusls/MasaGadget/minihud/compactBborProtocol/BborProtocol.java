package com.plusls.MasaGadget.minihud.compactBborProtocol;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.DisconnectEvent;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.minihud.util.StructureTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BborProtocol {
    public static final String NAMESPACE = "bbor";
    public static final ReentrantLock lock = new ReentrantLock(true);
    // recv
    private static final Identifier INITIALIZE = id("initialize");
    private static final Identifier ADD_BOUNDING_BOX_V2 = id("add_bounding_box_v2");

    // send
    private static final Identifier SUBSCRIBE = id("subscribe");

    private static final HashMap<Integer, String> BBOR_ID_TO_MINIHUD_ID = new HashMap<>();
    public static Map<Identifier, ListTag> structuresCache = null;
    public static Long seedCache = null;
    public static BlockPos spawnPos = null;
    public static boolean enable = false;
    public static boolean carpetOrServux = false;

    static {
        for (StructureTypes.StructureType type : StructureTypes.StructureType.values()) {
            String structureName = type.getStructureName();
            Identifier key = new Identifier(structureName.toLowerCase(Locale.ROOT));
            BBOR_ID_TO_MINIHUD_ID.put(structureName.hashCode(), key.toString());
            BBOR_ID_TO_MINIHUD_ID.put(lowVersionStructureName(structureName).hashCode(), key.toString());
        }
    }

    private static Identifier id(String path) {
        return new Identifier(NAMESPACE, path);
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
    }


    private static void onDisconnect() {
        ModInfo.LOGGER.info("BborProtocol onDisconnect");
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
            if (Configs.Minihud.COMPACT_BBOR_PROTOCOL.getBooleanValue()) {
                initMetaData();
            }
            ModInfo.LOGGER.info("init seed: {}", BborProtocol.seedCache);
            if (!ModInfo.isModLoaded(ModInfo.BBOR_MOD_ID)) {
                ModInfo.LOGGER.debug("SUBSCRIBE BBOR.");
                clientPlayNetworkHandler.sendPacket(new CustomPayloadC2SPacket(SUBSCRIBE, new PacketByteBuf(Unpooled.buffer())));
            }
        }
    }

    public static void bborInit(Identifier dimensionId) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
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
            structuresCache.put(dimensionId, new ListTag());
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

        CompoundTag tag = BoundingBoxDeserializer.deserializeStructure(buf);
        if (!structuresCache.containsKey(dimensionId)) {
            structuresCache.put(dimensionId, new ListTag());
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

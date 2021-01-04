package com.plusls.MasaGadget.network;

import com.plusls.MasaGadget.MasaGadgetMod;
import com.plusls.MasaGadget.util.BoundingBoxDeserializer;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.minihud.util.StructureType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class BborProtocol {
    private static final Identifier BBOR_INITIALIZE = new Identifier("bbor:initialize");
    private static final Identifier BBOR_ADD_BOUNDING_BOX_V2 = new Identifier("bbor:add_bounding_box_v2");
    private static final Identifier BBOR_SUBSCRIBE = new Identifier("bbor:subscribe");

    private static final HashMap<Integer, String> BBOR_ID_TO_MINIHUD_ID = new HashMap<>();
    public static ListTag structuresCache = null;
    public static Long seedCache = null;
    public static BlockPos spawnPos = null;
    public static boolean enable = false;
    public static boolean carpetOrServux = false;

    static {
        for (StructureType type : StructureType.VALUES) {
            String structureName = type.getStructureName();
            if (type.getFeature() != null) {
                Identifier key = Registry.STRUCTURE_FEATURE.getId(type.getFeature());
                if (key != null) {
                    BBOR_ID_TO_MINIHUD_ID.put(structureName.hashCode(), key.toString());
                }
            }
        }
    }

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register(BborProtocol::onJoinServer);
    }

    private static void onJoinServer(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (!MasaGadgetMod.bborCompat) {
            sender.sendPacket(BBOR_SUBSCRIBE, new PacketByteBuf(Unpooled.buffer()));
        }
    }

    public static void bborProtocolHandler(Identifier channel, PacketByteBuf data) {
        if (channel.equals(BBOR_INITIALIZE)) {
            bborInitializeHandler(data);
        } else if (channel.equals(BBOR_ADD_BOUNDING_BOX_V2)) {
            bborAddBoundingBoxV2Handler(data);
        }
    }

    private static void bborInitializeHandler(PacketByteBuf data) {
        long seed = data.readLong();
        int spawnX = data.readInt();
        int spawnZ = data.readInt();
        BborProtocol.seedCache = seed;
        BborProtocol.spawnPos = new BlockPos(spawnX, 0, spawnZ);
        BborProtocol.structuresCache = new ListTag();
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

        CompoundTag tag = BoundingBoxDeserializer.deserializeStructure(buf);

        if (tag != null) {
            ListTag structures = new ListTag();
            structures.add(tag);
            structuresCache.add(tag);
            if (enable) {
                DataStorage.getInstance().addOrUpdateStructuresFromServer(structures, 0x7fffffff - 0x1000, false);
            }
        }
    }
}

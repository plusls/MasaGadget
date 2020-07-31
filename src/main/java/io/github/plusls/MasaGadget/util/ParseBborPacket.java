package io.github.plusls.MasaGadget.util;

import fi.dy.masa.minihud.util.DataStorage;
<<<<<<< HEAD
import fi.dy.masa.minihud.util.StructureTypes;
import io.github.plusls.MasaGadget.MasaGadgetMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.HashMap;
import java.util.Locale;
=======
import fi.dy.masa.minihud.util.StructureType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
>>>>>>> 1.16.x

public class ParseBborPacket {
    private static final HashMap<Integer, String> BBOR_ID_TO_MINIHUD_ID = new HashMap<>();
    public static ListTag structuresCache = null;
<<<<<<< HEAD
    public static long seedCache = 0;
    public static BlockPos spawnPos = null;

    static {
        StructureTypes.StructureType[] structures = StructureTypes.StructureType.values();
        for (StructureTypes.StructureType structure : structures) {
            String structureName = structure.getStructureName();
            StructureFeature<?> feature = (StructureFeature) Feature.STRUCTURES.get(structureName.toLowerCase(Locale.ROOT));
            if (feature != null) {
                Identifier key = Registry.STRUCTURE_FEATURE.getId(feature);
=======
    public static Long seedCache = null;
    public static BlockPos spawnPos = null;
    public static final Lock lock = new ReentrantLock();

    static {
        for (StructureType type : StructureType.VALUES) {
            String structureName = type.getStructureName();
            if (type.getFeature() != null) {
                Identifier key = Registry.STRUCTURE_FEATURE.getId(type.getFeature());

>>>>>>> 1.16.x
                if (key != null) {
                    BBOR_ID_TO_MINIHUD_ID.put(structureName.hashCode(), key.toString());
                }
            }
<<<<<<< HEAD
        }
=======

        }

>>>>>>> 1.16.x
    }

    static public String bborIdToMinihudId(int bborId) {
        return BBOR_ID_TO_MINIHUD_ID.getOrDefault(bborId, "");
    }

    static public void parse(PacketByteBuf buf) {
        Identifier dimensionId = buf.readIdentifier();
        // MasaGadgetMod.LOGGER.info(dimensionId.toString());

        CompoundTag tag = null;
        tag = BoundingBoxDeserializer.deserializeStructure(buf);

        if (tag != null) {
            ListTag structures = new ListTag();
            structures.add(tag);
            structuresCache.add(tag);
            DataStorage.getInstance().addOrUpdateStructuresFromServer(structures, 0x7fffffff - 0x1000, false);
        }
    }
}

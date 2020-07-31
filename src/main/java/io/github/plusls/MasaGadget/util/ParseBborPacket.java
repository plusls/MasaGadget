package io.github.plusls.MasaGadget.util;

import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.minihud.util.StructureType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class ParseBborPacket {
    private static final HashMap<Integer, String> BBOR_ID_TO_MINIHUD_ID = new HashMap<>();
    public static ListTag structuresCache = null;
    public static long seedCache = 0;
    public static BlockPos spawnPos = null;

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

package io.github.plusls.MasaGadget.util;

import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.minihud.util.StructureType;
import io.github.plusls.MasaGadget.MasaGadgetMod;
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
    public static Long seedCache = null;
    public static BlockPos spawnPos = null;
    public static boolean enable = false;
    public static boolean carpetOrservux = false;

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

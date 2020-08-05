package io.github.plusls.MasaGadget.util;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;

public class BoundingBoxDeserializer {

    public static CompoundTag deserializeStructure(PacketByteBuf buf) {
        CompoundTag tag = new CompoundTag();

        deserializeStructureBox(buf, tag, true);
        if (!tag.contains("BB")) {
            return null;
        }

        ListTag childrenTagList = new ListTag();

        while (buf.isReadable()) {
            CompoundTag childrenTag = new CompoundTag();
            deserializeStructureBox(buf, childrenTag, false);
            childrenTagList.add(childrenTag);
        }

        tag.put("Children", childrenTagList);
        return tag;
    }

    private static void deserializeStructureBox(PacketByteBuf buf, CompoundTag tag, boolean first) {
        if (!buf.isReadable(2) || buf.readChar() != 'S')
            return;

        String typeId = ParseBborPacket.bborIdToMinihudId(buf.readInt());
        if (typeId.equals("")) {
            return;
        }

        int minX = buf.readVarInt();
        int minY = buf.readVarInt();
        int minZ = buf.readVarInt();
        int maxX = buf.readVarInt();
        int maxY = buf.readVarInt();
        int maxZ = buf.readVarInt();
        IntArrayTag boundingBox = new IntArrayTag(new int[]{minX, minY, minZ, maxX, maxY, maxZ});

        tag.put("BB", boundingBox);

        if (first) {
            tag.putString("id", typeId);
        }
        MasaGadgetMod.LOGGER.debug("deserializeresult: \"{}\" ({}, {}, {}) ({}, {}, {})", typeId, minX, minY, minZ, maxX, maxY, maxZ);
    }
}

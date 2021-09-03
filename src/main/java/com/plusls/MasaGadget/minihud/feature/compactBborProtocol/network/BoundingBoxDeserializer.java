package com.plusls.MasaGadget.minihud.feature.compactBborProtocol.network;

import com.plusls.MasaGadget.MasaGadgetMod;
import com.plusls.MasaGadget.ModInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;

public class BoundingBoxDeserializer {

    public static NbtCompound deserializeStructure(PacketByteBuf buf) {
        NbtCompound tag = new NbtCompound();

        deserializeStructureBox(buf, tag, true);
        if (!tag.contains("BB")) {
            return null;
        }

        NbtList childrenTagList = new NbtList();

        while (buf.isReadable()) {
            NbtCompound childrenTag = new NbtCompound();
            deserializeStructureBox(buf, childrenTag, false);
            childrenTagList.add(childrenTag);
        }

        tag.put("Children", childrenTagList);
        return tag;
    }

    private static void deserializeStructureBox(PacketByteBuf buf, NbtCompound tag, boolean first) {
        if (!buf.isReadable(2) || buf.readChar() != 'S')
            return;

        String typeId = BborProtocol.bborIdToMinihudId(buf.readInt());
        if (typeId.equals("")) {
            return;
        }

        int minX = buf.readVarInt();
        int minY = buf.readVarInt();
        int minZ = buf.readVarInt();
        int maxX = buf.readVarInt();
        int maxY = buf.readVarInt();
        int maxZ = buf.readVarInt();
        NbtIntArray boundingBox = new NbtIntArray(new int[]{minX, minY, minZ, maxX, maxY, maxZ});

        tag.put("BB", boundingBox);

        if (first) {
            tag.putString("id", typeId);
        }
        ModInfo.LOGGER.debug("deserializeresult: \"{}\" ({}, {}, {}) ({}, {}, {})", typeId, minX, minY, minZ, maxX, maxY, maxZ);
    }
}

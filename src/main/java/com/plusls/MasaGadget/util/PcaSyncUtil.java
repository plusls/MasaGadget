package com.plusls.MasaGadget.util;

import fi.dy.masa.litematica.selection.Box;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.PositionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PcaSyncUtil {
    @Nullable
    public static BlockPos lastUpdatePos = null;

    public static void sync(List<Box> boxes) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        PcaSyncUtil.lastUpdatePos = null;

        BlockPos lastUpdatePos = null;
        for (Box box : boxes) {

            BlockPos pos1 = box.getPos1();
            BlockPos pos2 = box.getPos2();
            if (pos1 == null || pos2 == null) {
                continue;
            }
            int maxX = Math.max(pos1.getX(), pos2.getX());
            int maxY = Math.max(pos1.getY(), pos2.getY());
            int maxZ = Math.max(pos1.getZ(), pos2.getZ());
            int minX = Math.min(pos1.getX(), pos2.getX());
            int minY = Math.min(pos1.getY(), pos2.getY());
            int minZ = Math.min(pos1.getZ(), pos2.getZ());

            // 参考 PositionUtils。createAABBFrom
            List<Entity> entities = world.getEntities((Entity) null,
                    PositionUtils.createAABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1), EntityUtils.NOT_PLAYER);
            for (Entity entity : entities) {
                if (entity instanceof Container) {
                    PcaSyncProtocol.syncEntity(entity.getId());
                }
            }
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (world.getBlockEntity(pos) != null) {
                            lastUpdatePos = pos;
                            PcaSyncProtocol.syncBlockEntity(pos);
                        }
                    }
                }
            }

        }
        PcaSyncProtocol.cancelSyncBlockEntity();
        PcaSyncProtocol.cancelSyncEntity();
        PcaSyncUtil.lastUpdatePos = lastUpdatePos;
    }
}

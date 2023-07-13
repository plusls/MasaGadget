package com.plusls.MasaGadget.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class SyncUtil {
    /**
     * Try to get entity data from the integrated server.
     *
     * @return Return synced data if the access is successful, otherwise return input entity.
     */
    public static Entity syncEntityDataFromIntegratedServer(Entity entity) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();

        if (server == null) {
            return entity;
        }

        //#if MC > 11502
        ServerLevel level = server.getLevel(entity.getLevelCompat().dimension());
        //#else
        //$$ ServerLevel level = server.getLevel(entity.dimension);
        //#endif

        if (level == null) {
            return entity;
        }

        Entity localEntity = level.getEntity(entity.getId());
        return localEntity == null ? entity : localEntity;
    }
}

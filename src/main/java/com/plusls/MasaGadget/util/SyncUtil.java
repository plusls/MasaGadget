package com.plusls.MasaGadget.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.EntityCompat;

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

        EntityCompat entityCompat = EntityCompat.of(entity);

        ServerLevel level = server.getLevel(
                //#if MC > 11502
                entityCompat.getLevel().dimension()
                //#else
                //$$ entity.dimension
                //#endif
        );

        if (level == null) {
            return entity;
        }

        Entity localEntity = level.getEntity(entity.getId());
        return localEntity == null ? entity : localEntity;
    }
}

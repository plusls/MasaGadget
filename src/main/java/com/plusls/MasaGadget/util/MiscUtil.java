package com.plusls.MasaGadget.util;

import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class MiscUtil {

    @SuppressWarnings("unchecked")
    public static <T extends Entity> T getBestEntity(T entity) {
        // Only try to fetch the corresponding server world if the entity is in the actual client world.
        // Otherwise the entity may be for example in Litematica's schematic world.
        World world = entity.getEntityWorld();
        MinecraftClient client = MinecraftClient.getInstance();
        T ret = entity;
        if (world == client.world) {
            world = WorldUtils.getBestWorld(client);
            if (world != null && world != client.world) {
                Entity bestEntity = world.getEntityById(entity.getEntityId());
                if (entity.getClass().isInstance(bestEntity)) {
                    ret = (T) bestEntity;
                }
            }
        }
        return ret;
    }
}

package com.plusls.MasaGadget.util;

import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


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

    @Nullable
    public static String getTranslatedOrFallback(String key, @Nullable String fallback) {
        String translated = I18n.translate(key);

        if (!key.equals(translated)) {
            return translated;
        }

        return fallback;
    }

    public static String getStringWithoutFormat(String text) {
        StringBuilder ret = new StringBuilder(text);
        if (text.contains("§")) {
            ret = new StringBuilder();
            for (int i = 0; i < text.length(); ++i) {
                if (text.charAt(i) == '§') {
                    ++i;
                    continue;
                }
                ret.append(text.charAt(i));
            }
        }
        return ret.toString();
    }
}

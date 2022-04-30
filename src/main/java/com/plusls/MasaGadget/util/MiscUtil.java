package com.plusls.MasaGadget.util;

import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import top.hendrixshen.magiclib.language.I18n;

import javax.annotation.Nullable;

public class MiscUtil {

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static <T extends Entity> T getBestEntity(T entity) {
        // Only try to fetch the corresponding server world if the entity is in the actual client world.
        // Otherwise the entity may be for example in Litematica's schematic world.
        Level world = entity.getCommandSenderWorld();
        Minecraft client = Minecraft.getInstance();
        T ret = entity;
        if (world == client.level) {
            world = WorldUtils.getBestWorld(client);
            if (world != null && world != client.level) {
                Entity bestEntity = world.getEntity(entity.getId());
                if (entity.getClass().isInstance(bestEntity)) {
                    ret = MiscUtil.cast(bestEntity);
                }
            }
        }
        return ret;
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

    @Nullable
    public static String getTranslatedOrFallback(String key, @Nullable String fallback, Object... objects) {
        String translated = I18n.get(key, objects);
        return !key.equals(translated) ? translated : fallback;
    }
}

package com.plusls.MasaGadget;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.compat.minecraft.api.network.chat.ComponentCompatApi;
import top.hendrixshen.magiclib.malilib.impl.ConfigHandler;
import top.hendrixshen.magiclib.language.api.I18n;

//#if MC > 11502
import net.minecraft.network.chat.MutableComponent;
//#else
//$$ import net.minecraft.network.chat.BaseComponent;
//#endif

public class ModInfo {
    public static final String ITEMSCROLLER_MOD_ID = "itemscroller";
    public static final String LITEMATICA_MOD_ID = "litematica";
    public static final String MINIHUD_MOD_ID = "minihud";
    public static final String MODMENU_MOD_ID = "modmenu";
    public static final String OMMC_MOD_ID = "ommc";
    public static final String TWEAKEROO_MOD_ID = "tweakeroo";
    public static String MOD_ID = "@MOD_IDENTIFIER@";
    public static final String CURRENT_MOD_ID = "@MOD_IDENTIFIER@-@MINECRAFT_VERSION_IDENTIFY@";
    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(CURRENT_MOD_ID)
            .orElseThrow(RuntimeException::new).getMetadata().getName();
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(CURRENT_MOD_ID)
            .orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static ConfigHandler configHandler;

    public static String translate(String key, Object... objects) {
        return I18n.get(ModInfo.MOD_ID + "." + key, objects);
    }

    public static @NotNull
    //#if MC > 11502
    MutableComponent
    //#else
    //$$ BaseComponent
    //#endif
    translatable(String key, Object... objects) {
        return ComponentCompatApi.translatable(ModInfo.MOD_ID + "." + key, objects);
    }

    @Contract("_ -> new")
    public static @NotNull ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}


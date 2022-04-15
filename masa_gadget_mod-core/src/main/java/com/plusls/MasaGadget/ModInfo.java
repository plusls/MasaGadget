package com.plusls.MasaGadget;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.hendrixshen.magiclib.config.ConfigHandler;

import java.util.Optional;

public class ModInfo {
    public static final String MALILIB_MOD_ID = "malilib";

    public static final String TWEAKEROO_MOD_ID = "tweakeroo";
    public static final String MINIHUD_MOD_ID = "minihud";
    public static final String LITEMATICA_MOD_ID = "litematica";
    public static final String ITEMSCROLLER_MOD_ID = "itemscroller";
    public static final String MODMENU_MOD_ID = "modmenu";
    public static final String CARPET_TIS_ADDITION_MOD_ID = "carpet-tis-addition";
    public static String MOD_ID = "masa_gadget_mod";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static String MOD_VERSION;

    public static ConfigHandler configHandler;

    static {
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(MOD_ID);
        modContainerOptional.ifPresent(modContainer -> MOD_VERSION = modContainer.getMetadata().getVersion().getFriendlyString());
    }

    public static String translate(String key, Object... objects) {
        return I18n.get(ModInfo.MOD_ID + "." + key, objects);
    }


    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}


package com.plusls.MasaGadget;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.hendrixshen.magiclib.config.ConfigHandler;
import top.hendrixshen.magiclib.language.I18n;

public class ModInfo {

    public static final String TWEAKEROO_MOD_ID = "tweakeroo";
    public static final String MINIHUD_MOD_ID = "minihud";
    public static final String LITEMATICA_MOD_ID = "litematica";
    public static final String ITEMSCROLLER_MOD_ID = "itemscroller";
    public static final String MODMENU_MOD_ID = "modmenu";
    public static String MOD_ID = "masa_gadget_mod";

    //#if MC > 11802
    //$$ public static final String CURRENT_MOD_ID = MOD_ID + "-snapshot";
    //#elseif MC > 11701
    public static final String CURRENT_MOD_ID = MOD_ID + "-1_18_2";
    //#elseif MC > 11605
    //$$ public static final String CURRENT_MOD_ID = MOD_ID + "-1_17_1";
    //#elseif MC > 11502
    //$$ public static final String CURRENT_MOD_ID = MOD_ID + "-1_16_5";
    //#elseif MC > 11404
    //$$ public static final String CURRENT_MOD_ID = MOD_ID + "-1_15_2";
    //#else
    //$$ public static final String CURRENT_MOD_ID = MOD_ID + "-1_14_4";
    //#endif

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(CURRENT_MOD_ID)
            .orElseThrow(RuntimeException::new).getMetadata().getName();
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(CURRENT_MOD_ID)
            .orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();


    public static ConfigHandler configHandler;

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


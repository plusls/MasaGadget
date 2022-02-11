package com.plusls.MasaGadget;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ModInfo {
    public static final String TWEAKEROO_MOD_ID = "tweakeroo";
    public static final String MINIHUD_MOD_ID = "minihud";
    public static final String LITEMATICA_MOD_ID = "litematica";
    public static final String ITEMSCROLLER_MOD_ID = "itemscroller";
    public static final String MODMENU_MOD_ID = "modmenu";
    public static final String BBOR_MOD_ID = "bbor";
    public static final String CARPET_TIS_ADDITION_MOD_ID = "carpet-tis-addition";
    public static String MOD_ID = "masa_gadget_mod";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static String MOD_VERSION;

    static {
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(MOD_ID);
        modContainerOptional.ifPresent(modContainer -> MOD_VERSION = modContainer.getMetadata().getVersion().getFriendlyString());
    }

    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}


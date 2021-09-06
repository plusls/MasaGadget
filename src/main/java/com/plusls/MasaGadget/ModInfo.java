package com.plusls.MasaGadget;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModInfo {
    public static String MOD_ID = "masa_gadget_mod";
    public static final String MOD_NAME = "MasaGadget";
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().toString();
    public static final String MOD_VERSION_TYPE = "Version Exception";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}


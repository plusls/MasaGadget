package com.plusls.MasaGadget;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModInfo {
    public static String MOD_ID = "masa_gadget_mod";
    public static final String MOD_NAME = "MasaGadget";
    public static final String MOD_VERSION = "2.0.0-build.undefined";
    public static final String MOD_VERSION_TYPE = "Version Exception";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}


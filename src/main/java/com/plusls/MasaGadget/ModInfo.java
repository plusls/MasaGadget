package com.plusls.MasaGadget;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ModInfo {
    public static String MOD_ID = "masa_gadget_mod";
    public static String MOD_VERSION;
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    static {
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(MOD_ID);
        modContainerOptional.ifPresent(modContainer -> MOD_VERSION = modContainer.getMetadata().getVersion().getFriendlyString());
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}


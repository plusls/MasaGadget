package com.plusls.MasaGadget;

import com.plusls.MasaGadget.malilib.feature.compactBborProtocol.network.BborProtocol;
import com.plusls.MasaGadget.tweakeroo.feature.pcaSyncProtocol.network.PcaSyncProtocol;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class MasaGadgetMod implements ClientModInitializer {
    public static final String MODID = "masa_gadget_mod";
    public static final Logger LOGGER = LogManager.getLogger("MasaGadgetMod");
    public static String level = "INFO";

    @Override
    public void onInitializeClient() {
        Configurator.setLevel(LOGGER.getName(), Level.toLevel(MasaGadgetMod.level));
        if (MasaGadgetMixinPlugin.isBborLoaded) {
            LOGGER.info("BBOR detected.");
        }

        if (MasaGadgetMixinPlugin.isTweakerooLoaded) {
            PcaSyncProtocol.init();
        }
        if (MasaGadgetMixinPlugin.isMinihudLoaded) {
            BborProtocol.init();
        }
    }

    public static Identifier id(String id) {
        return new Identifier(MODID, id);
    }
}

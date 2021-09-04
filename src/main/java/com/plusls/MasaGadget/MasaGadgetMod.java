package com.plusls.MasaGadget;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.InputHandler;
import com.plusls.MasaGadget.minihud.compactBborProtocol.BborProtocol;
import com.plusls.MasaGadget.tweakeroo.feature.pcaSyncProtocol.network.PcaSyncProtocol;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class MasaGadgetMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (MasaGadgetMixinPlugin.isBborLoaded) {
            ModInfo.LOGGER.info("BBOR detected.");
        }

        if (MasaGadgetMixinPlugin.isTweakerooLoaded) {
            PcaSyncProtocol.init();
        }
        if (MasaGadgetMixinPlugin.isMinihudLoaded) {
            BborProtocol.init();
        }
        com.plusls.MasaGadget.tweakeroo.feature.inventoryPreviewSupportSelect.InputHandler.register();
        ConfigManager.getInstance().registerConfigHandler(ModInfo.MOD_ID, new Configs());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
    }
}

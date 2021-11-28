package com.plusls.MasaGadget;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.InputHandler;
import com.plusls.MasaGadget.minihud.compactBborProtocol.BborProtocol;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.MouseScrollInputHandler;
import com.plusls.MasaGadget.tweakeroo.pcaSyncProtocol.PcaSyncProtocol;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class MasaGadgetMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (ModInfo.isModLoaded(ModInfo.BBOR_MOD_ID)) {
            ModInfo.LOGGER.info("BBOR detected.");
        }
        PcaSyncProtocol.init();
        if (ModInfo.isModLoaded(ModInfo.MINIHUD_MOD_ID)) {
            BborProtocol.init();
        }
        MouseScrollInputHandler.register();
        ConfigManager.getInstance().registerConfigHandler(ModInfo.MOD_ID, new Configs());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
    }
}

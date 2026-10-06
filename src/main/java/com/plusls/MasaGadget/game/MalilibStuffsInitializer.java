package com.plusls.MasaGadget.game;

import com.plusls.MasaGadget.SharedConstants;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 26.3
//$$ import fi.dy.masa.malilib.registry.Registry;
//$$ import fi.dy.masa.malilib.util.data.ModInfo;
//#endif

//#if FORGE_LIKE
//$$ import top.hendrixshen.magiclib.util.minecraft.ForgePlatformUtil;
//#endif
// CHECKSTYLE.ON: ImportOrder

public class MalilibStuffsInitializer {
    public static void init() {
        InitializationHandler.getInstance().registerInitializationHandler(() -> {
            ConfigManager.getInstance().registerConfigHandler(SharedConstants.getModIdentifier(),
                    SharedConstants.getConfigHandler());
            //#if MC >= 26.3
            //$$ Registry.CONFIG_SCREEN.registerConfigScreenFactory(new ModInfo(
            //$$         SharedConstants.getModIdentifier(), SharedConstants.getModName(), ConfigGui::new));
            //#endif
        });
        Configs.init();
        InputEventHandler.getKeybindManager().registerKeybindProvider(
                (IKeybindProvider) SharedConstants.getConfigManager());
        //#if FORGE_LIKE
        //$$ MalilibStuffsInitializer.setupForgeConfigGui();
        //#endif
    }

    //#if FORGE_LIKE
    //$$ private static void setupForgeConfigGui() {
    //$$     ForgePlatformUtil.registerModConfigScreen(SharedConstants.getModIdentifier(),
    //$$             screen -> {
    //$$                 ConfigGui gui = new ConfigGui();
    //#if MC > 11903
    //$$                 gui.setParent(screen);
    //#else
    //$$                 gui.setParentGui(screen);
    //#endif
    //$$                 return gui;
    //$$             });
    //$$ }
    //#endif
}

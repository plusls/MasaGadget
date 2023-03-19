package com.plusls.MasaGadget.gui;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import lombok.Getter;
import top.hendrixshen.magiclib.malilib.impl.ConfigManager;
import top.hendrixshen.magiclib.malilib.impl.gui.ConfigGui;

public class GuiConfigs extends ConfigGui {
    @Getter(lazy = true)
    private static final GuiConfigs instance = new GuiConfigs(ModInfo.MOD_ID, Configs.ConfigCategory.GENERIC, ConfigManager.get(ModInfo.MOD_ID));

    private GuiConfigs(String identifier, String defaultTab, ConfigManager configManager) {
        super(identifier, defaultTab, configManager, () -> ModInfo.translate("gui.title.configs", ModInfo.MOD_VERSION));
    }
}
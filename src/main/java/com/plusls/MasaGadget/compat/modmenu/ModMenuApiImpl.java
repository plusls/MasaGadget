package com.plusls.MasaGadget.compat.modmenu;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.gui.GuiConfigs;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import top.hendrixshen.magiclib.compat.modmenu.ModMenuApiCompat;

public class ModMenuApiImpl implements ModMenuApi, ModMenuApiCompat {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {

        return (screen) -> {
            GuiConfigs gui = GuiConfigs.getInstance();
            gui.setParentGui(screen);
            return gui;
        };
    }

    @Override
    public String getModId() {
        return ModInfo.MOD_ID;
    }
}
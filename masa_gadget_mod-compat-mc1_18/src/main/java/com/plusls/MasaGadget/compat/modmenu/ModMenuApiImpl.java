package com.plusls.MasaGadget.compat.modmenu;

import com.plusls.MasaGadget.gui.GuiConfigs;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {

        return (screen) -> {
            GuiConfigs gui = GuiConfigs.getInstance();
            gui.setParentGui(screen);
            return gui;
        };
    }
}
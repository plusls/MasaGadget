package com.plusls.MasaGadget.compat.modmenu;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.gui.GuiConfigs;
import com.terraformersmc.modmenu.api.ModMenuApi;
import top.hendrixshen.magiclib.compat.modmenu.ModMenuCompatApi;

public class ModMenuApiImpl implements ModMenuApi, ModMenuCompatApi {
    @Override
    public ConfigScreenFactoryCompat<?> getConfigScreenFactoryCompat() {
        return (screen) -> {
            GuiConfigs gui = GuiConfigs.getInstance();
            gui.setParentGui(screen);
            return gui;
        };
    }

    @Override
    public String getModIdCompat() {
        return ModInfo.MOD_ID;
    }

}
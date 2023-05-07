package com.plusls.MasaGadget.compat.modmenu;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.gui.GuiConfigs;
import top.hendrixshen.magiclib.compat.modmenu.ModMenuCompatApi;

public class ModMenuApiImpl implements ModMenuCompatApi {
    @Override
    public ConfigScreenFactoryCompat<?> getConfigScreenFactoryCompat() {
        return (screen) -> {
            GuiConfigs gui = GuiConfigs.getInstance();
            //#if MC > 11903 && MC < 12000
            gui.setParent(screen);
            //#else
            //$$ gui.setParentGui(screen);
            //#endif
            return gui;
        };
    }

    @Override
    public String getModIdCompat() {
        return ModInfo.CURRENT_MOD_ID;
    }
}

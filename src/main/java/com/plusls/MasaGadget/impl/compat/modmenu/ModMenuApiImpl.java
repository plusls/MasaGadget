//#if FABRIC_LIKE
package com.plusls.MasaGadget.impl.compat.modmenu;

import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.game.ConfigGui;
import top.hendrixshen.magiclib.api.compat.modmenu.ModMenuApiCompat;

public class ModMenuApiImpl implements ModMenuApiCompat {
    @Override
    public ConfigScreenFactoryCompat<?> getConfigScreenFactoryCompat() {
        return (screen) -> {
            ConfigGui configGui = new ConfigGui();
            //#if MC > 11903
            //$$ configGui.setParent(screen);
            //#else
            configGui.setParentGui(screen);
            //#endif
            return configGui;
        };
    }

    @Override
    public String getModIdCompat() {
        return SharedConstants.getModIdentifier();
    }
}
//#endif

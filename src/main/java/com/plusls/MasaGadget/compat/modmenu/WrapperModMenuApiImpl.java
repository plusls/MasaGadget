package com.plusls.MasaGadget.compat.modmenu;

import com.plusls.MasaGadget.ModInfo;

public class WrapperModMenuApiImpl extends ModMenuApiImpl {
    @Override
    public String getModIdCompat() {
        return ModInfo.MOD_ID;
    }
}

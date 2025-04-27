package com.plusls.MasaGadget.game;

import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.pinyinSousuo.PinInHelper;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigManagerImpl;

public class MasaGadgetManager extends MagicConfigManagerImpl {
    public MasaGadgetManager() {
        super(SharedConstants.getModIdentifier());
    }

    @Override
    public void onConfigLoaded() {
        PinInHelper.getInstance().commitConfig();
    }
}

package com.plusls.MasaGadget.impl.mod_tweak.malilib.pinyinSouSuo;

import com.plusls.MasaGadget.SharedConstants;
import top.hendrixshen.magiclib.api.malilib.config.option.EnumOptionEntry;

public enum PinYinSouSuoKeyboard implements EnumOptionEntry {
    QUANPIN,
    DAQIAN,
    XIAOHE,
    ZIRANMA,
    SOUGOU,
    GUOBIAO,
    MICROSOFT,
    PINYINPP,
    ZIGUANG;

    public static final PinYinSouSuoKeyboard DEFAULT = PinYinSouSuoKeyboard.QUANPIN;

    @Override
    public EnumOptionEntry[] getAllValues() {
        return PinYinSouSuoKeyboard.values();
    }

    @Override
    public EnumOptionEntry getDefault() {
        return PinYinSouSuoKeyboard.DEFAULT;
    }

    @Override
    public String getTranslationPrefix() {
        return SharedConstants.getModIdentifier().concat(".config.option.pinyinSousuoKeyboard");
    }
}

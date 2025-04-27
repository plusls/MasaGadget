package com.plusls.MasaGadget.impl.mod_tweak.malilib.pinyinSouSuo;

import com.google.common.collect.ImmutableMap;
import com.plusls.MasaGadget.game.Configs;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.towdium.pinin.Keyboard;
import me.towdium.pinin.PinIn;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PinInHelper {
    @Getter(lazy = true)
    private static final PinInHelper instance = new PinInHelper();
    private static final Map<PinYinSouSuoKeyboard, Keyboard> keyboardMapping = ImmutableMap.<PinYinSouSuoKeyboard, Keyboard>builder()
            .put(PinYinSouSuoKeyboard.QUANPIN, Keyboard.QUANPIN)
            .put(PinYinSouSuoKeyboard.DAQIAN, Keyboard.DAQIAN)
            .put(PinYinSouSuoKeyboard.XIAOHE, Keyboard.XIAOHE)
            .put(PinYinSouSuoKeyboard.ZIRANMA, Keyboard.ZIRANMA)
            .put(PinYinSouSuoKeyboard.SOUGOU, Keyboard.SOUGOU)
            .put(PinYinSouSuoKeyboard.GUOBIAO, Keyboard.GUOBIAO)
            .put(PinYinSouSuoKeyboard.MICROSOFT, Keyboard.MICROSOFT)
            .put(PinYinSouSuoKeyboard.PINYINPP, Keyboard.PINYINPP)
            .put(PinYinSouSuoKeyboard.ZIGUANG, Keyboard.ZIGUANG)
            .build();

    private final PinIn pinIn = new PinIn().config().accelerate(true).commit();

    public void commitConfig() {
        PinIn.Config config = this.pinIn.config();
        config.keyboard(PinInHelper.keyboardMapping.getOrDefault((PinYinSouSuoKeyboard) Configs.pinyinSouSuoKeyboard.getOptionListValue(), Keyboard.QUANPIN));
        config.fZh2Z(Configs.pinyinSouSuoFZh2Z.getBooleanValue());
        config.fSh2S(Configs.pinyinSouSuoFSh2S.getBooleanValue());
        config.fCh2C(Configs.pinyinSouSuoFCh2C.getBooleanValue());
        config.fAng2An(Configs.pinyinSouSuoFAng2An.getBooleanValue());
        config.fIng2In(Configs.pinyinSouSuoFIng2In.getBooleanValue());
        config.fEng2En(Configs.pinyinSouSuoFEng2En.getBooleanValue());
        config.fU2V(Configs.pinyinSouSuoFU2V.getBooleanValue());
        config.commit();
    }

    public boolean contains(String s1, String s2) {
        return this.pinIn.contains(s1, s2);
    }
}

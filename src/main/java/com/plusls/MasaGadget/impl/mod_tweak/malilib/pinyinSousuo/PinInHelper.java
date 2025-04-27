package com.plusls.MasaGadget.impl.mod_tweak.malilib.pinyinSousuo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.towdium.pinin.PinIn;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PinInHelper {
    @Getter(lazy = true)
    private static final PinInHelper instance = new PinInHelper();

    private final PinIn pinIn = new PinIn().config().accelerate(true).commit();

    public void commitConfig() {
        PinIn.Config config = this.pinIn.config();
        System.out.println("commit config");
    }

    public boolean contains(String s1, String s2) {
        return this.pinIn.contains(s1, s2);
    }
}

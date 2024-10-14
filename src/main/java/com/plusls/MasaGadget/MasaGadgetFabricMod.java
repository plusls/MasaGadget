//#if FABRIC_LIKE
package com.plusls.MasaGadget;

import net.fabricmc.api.ClientModInitializer;

public class MasaGadgetFabricMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MasaGadgetMod.onInitializeClient();
    }
}
//#endif

package com.plusls.MasaGadget;

import com.plusls.MasaGadget.game.MalilibStuffsInitializer;
import com.plusls.MasaGadget.impl.feature.entityInfo.EntityInfoRenderer;
import com.plusls.MasaGadget.impl.feature.entityTrace.EntityTraceRenderer;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.fastSwitchMasaConfigGui.FastMasaGuiSwitcher;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport.MalilibFavoritesData;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportComparator.ComparatorInfo;
import com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportSelect.MouseScrollInputHandler;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.event.minecraft.MinecraftListener;

public class MasaGadgetMod implements MinecraftListener {
    private static final MasaGadgetMod instance = new MasaGadgetMod();

    public static void onInitializeClient() {
        MagicLib.getInstance().getEventManager().register(MinecraftListener.class, MasaGadgetMod.instance);
        MalilibStuffsInitializer.init();
        PcaSyncProtocol.init();
        ComparatorInfo.getInstance().init();
        MouseScrollInputHandler.getInstance().init();
        EntityInfoRenderer.getInstance().init();
        EntityTraceRenderer.getInstance().init();
        HitResultHandler.getInstance().init();
        ComparatorInfo.getInstance().init();
    }

    @Override
    public void postInit() {
        FastMasaGuiSwitcher.getInstance().init();
        SharedConstants.getConfigHandler().registerExternalData("malilib_favorites", MalilibFavoritesData.getInstance());
    }
}

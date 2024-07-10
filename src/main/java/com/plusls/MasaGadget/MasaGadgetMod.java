package com.plusls.MasaGadget;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.feature.entityInfo.EntityInfoRenderer;
import com.plusls.MasaGadget.impl.feature.entityTrace.EntityTraceRenderer;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.fastSwitchMasaConfigGui.FastMasaGuiSwitcher;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport.MalilibFavoritesData;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportComparator.ComparatorInfo;
import com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportSelect.MouseScrollInputHandler;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import net.fabricmc.api.ClientModInitializer;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.event.minecraft.MinecraftListener;

public class MasaGadgetMod implements ClientModInitializer, MinecraftListener {
    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(() ->
                ConfigManager.getInstance().registerConfigHandler(SharedConstants.getModIdentifier(),
                        SharedConstants.getConfigHandler()));
        Configs.init();
        InputEventHandler.getKeybindManager().registerKeybindProvider(
                (IKeybindProvider) SharedConstants.getConfigManager());
        PcaSyncProtocol.init();
        MouseScrollInputHandler.getInstance().init();
        EntityInfoRenderer.getInstance().init();
        EntityTraceRenderer.getInstance().init();
        HitResultHandler.getInstance().init();
        ComparatorInfo.getInstance().init();
        MagicLib.getInstance().getEventManager().register(MinecraftListener.class, this);
    }

    @Override
    public void postInit() {
        FastMasaGuiSwitcher.getInstance().init();
        SharedConstants.getConfigHandler().registerExternalData("malilib_favorites", MalilibFavoritesData.getInstance());
    }
}

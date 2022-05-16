package com.plusls.MasaGadget;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.generic.renderNextRestockTime.NextRestockTimeRenderer;
import com.plusls.MasaGadget.generic.renderTradeEnchantedBook.TradeEnchantedBookRenderer;
import com.plusls.MasaGadget.generic.renderZombieVillagerConvertTime.ZombieVillagerConvertTimeRenderer;
import com.plusls.MasaGadget.util.HitResultUtil;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.MouseScrollInputHandler;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.fabricmc.api.ClientModInitializer;
import top.hendrixshen.magiclib.config.ConfigHandler;
import top.hendrixshen.magiclib.config.ConfigManager;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

public class MasaGadgetMod implements ClientModInitializer {
    private static final int CONFIG_VERSION = 1;

    @Dependencies(and = {
            //#if MC > 11701
            @Dependency(value = "itemscoller", versionPredicate = ">=0.16.0", optional = true),
            @Dependency(value = "litematica", versionPredicate = ">=0.11.0", optional = true),
            @Dependency(value = "minihud", versionPredicate = ">=0.22.0", optional = true),
            @Dependency(value = "tweakeroo", versionPredicate = ">=0.13.1", optional = true)
            //#elseif MC > 11605
            //$$ @Dependency(value = "itemscoller", versionPredicate = ">=0.15.0-dev.20211201.010054", optional = true),
            //$$ @Dependency(value = "litematica", versionPredicate = ">=0.9.0", optional = true),
            //$$ @Dependency(value = "minihud", versionPredicate = ">=0.20.0", optional = true),
            //$$ @Dependency(value = "tweakeroo", versionPredicate = ">=0.11.0", optional = true)
            //#elseif MC > 11502
            //$$ @Dependency(value = "itemscoller", versionPredicate = ">=0.15.0-dev.20210917.191808", optional = true),
            //$$ @Dependency(value = "litematica", versionPredicate = ">=0.0.0-dev.20210917.192300", optional = true),
            //$$ @Dependency(value = "minihud", versionPredicate = ">=0.19.0-dev.20210917.191825", optional = true),
            //$$ @Dependency(value = "tweakeroo", versionPredicate = ">=0.10.0-dev.20210917.191839", optional = true)
            //#elseif MC > 11404
            //$$ @Dependency(value = "itemscoller", versionPredicate = ">=0.15.0-dev.20200212.183513", optional = true),
            //$$ @Dependency(value = "litematica", versionPredicate = ">=0.0.0-dev.20200515.184506", optional = true),
            //$$ @Dependency(value = "minihud", versionPredicate = ">=0.19.0-dev.20200427.222110", optional = true),
            //$$ @Dependency(value = "tweakeroo", versionPredicate = ">=0.10.0-dev.20200424.222527", optional = true)
            //#else
            //$$ @Dependency(value = "itemscoller", versionPredicate = ">=0.15.0-dev.20190720.190250", optional = true),
            //$$ @Dependency(value = "litematica", versionPredicate = ">=0.0.0-dev.20191222.014040", optional = true),
            //$$ @Dependency(value = "minihud", versionPredicate = ">=0.19.0-dev.20191007.003640", optional = true),
            //$$ @Dependency(value = "tweakeroo", versionPredicate = ">=0.10.0-dev.20190903.193019", optional = true)
            //#endif
    })
    @Override
    public void onInitializeClient() {
        ConfigManager cm = ConfigManager.get(ModInfo.MOD_ID);
        cm.parseConfigClass(Configs.class);
        ModInfo.configHandler = new ConfigHandler(ModInfo.MOD_ID, cm, CONFIG_VERSION);
        ModInfo.configHandler.preDeserializeCallback = Configs::preDeserialize;
        ModInfo.configHandler.postSerializeCallback = Configs::postSerialize;
        ConfigHandler.register(ModInfo.configHandler);
        Configs.init(cm);
        PcaSyncProtocol.init();
        MouseScrollInputHandler.register();
        NextRestockTimeRenderer.init();
        TradeEnchantedBookRenderer.init();
        ZombieVillagerConvertTimeRenderer.init();
        HitResultUtil.init();
    }

}
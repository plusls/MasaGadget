package com.plusls.MasaGadget;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.generic.renderNextRestockTime.NextRestockTimeRenderer;
import com.plusls.MasaGadget.generic.renderTradeEnchantedBook.TradeEnchantedBookRenderer;
import com.plusls.MasaGadget.generic.renderZombieVillagerConvertTime.ZombieVillagerConvertTimeRenderer;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.MouseScrollInputHandler;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.fabricmc.api.ClientModInitializer;
import top.hendrixshen.magiclib.config.ConfigHandler;
import top.hendrixshen.magiclib.config.ConfigManager;

public class MasaGadgetMod implements ClientModInitializer {
    private static final int CONFIG_VERSION = 1;


    @Override
    public void onInitializeClient() {
        ConfigManager cm = ConfigManager.get(ModInfo.MOD_ID);
        cm.parseConfigClass(Configs.class);
        ModInfo.configHandler = new ConfigHandler(ModInfo.MOD_ID, cm, CONFIG_VERSION, Configs::preDeserialize,
                Configs::postSerialize);
        ConfigHandler.register(ModInfo.configHandler);
        Configs.init(cm);
        PcaSyncProtocol.init();
        MouseScrollInputHandler.register();
        NextRestockTimeRenderer.init();
        TradeEnchantedBookRenderer.init();
        ZombieVillagerConvertTimeRenderer.init();
    }

}

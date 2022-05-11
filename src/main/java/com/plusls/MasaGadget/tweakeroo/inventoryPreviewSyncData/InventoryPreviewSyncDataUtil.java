package com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncData;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;

public class InventoryPreviewSyncDataUtil {
    public static void onStateChangedCallback(Boolean oldStatus) {
        Minecraft mc = Minecraft.getInstance();
        if (!Configs.inventoryPreviewSyncData ||
                !PcaSyncProtocol.enable ||
                mc.hasSingleplayerServer() ||
                !FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() ||
                oldStatus
        ) {
            PcaSyncProtocol.cancelSyncBlockEntity();
            PcaSyncProtocol.cancelSyncEntity();
        }
    }
}

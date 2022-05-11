package com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncDataClientOnly;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;

import java.util.Objects;

public class InventoryPreviewSyncDataClientOnlyUtil {
    public static void onStateChangedCallback(Boolean oldStatus) {
        Minecraft mc = Minecraft.getInstance();
        if (!Configs.inventoryPreviewSyncDataClientOnly ||
                (Configs.inventoryPreviewSyncData && PcaSyncProtocol.enable) ||
                mc.hasSingleplayerServer() ||
                !FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() ||
                oldStatus
        ) {
            Objects.requireNonNull(Minecraft.getInstance().player).closeContainer();
        }
    }
}

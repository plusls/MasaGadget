package com.plusls.MasaGadget.tweakeroo;

import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncData.InventoryPreviewSyncDataUtil;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncDataClientOnly.InventoryPreviewSyncDataClientOnlyUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class InventoryPreviewUtil {

    private static final Set<Consumer<Boolean>> callbacks = new HashSet<>();
    private static boolean lastInventoryPreviewStatus = false;

    static {
        registerStatusChangedCallback(InventoryPreviewSyncDataUtil::onStateChangedCallback);
        registerStatusChangedCallback(InventoryPreviewSyncDataClientOnlyUtil::onStateChangedCallback);
    }

    public static void setLastInventoryPreviewStatus(boolean status) {
        if (lastInventoryPreviewStatus != status) {
            for (Consumer<Boolean> callback : callbacks) {
                callback.accept(lastInventoryPreviewStatus);
            }
            lastInventoryPreviewStatus = status;
        }
    }


    public static void registerStatusChangedCallback(Consumer<Boolean> callback) {
        callbacks.add(callback);
    }
}

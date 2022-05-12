package com.plusls.MasaGadget.tweakeroo;

import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncData.InventoryPreviewSyncDataUtil;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncDataClientOnly.InventoryPreviewSyncDataClientOnlyUtil;
import com.plusls.MasaGadget.util.TraceUtil;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class InventoryPreviewUtil {

    private static final Set<TraceCallback> traceCallbacks = new HashSet<>();
    private static boolean lastInventoryPreviewStatus = false;

    public static void init() {
        ClientTickEvents.END_WORLD_TICK.register(InventoryPreviewUtil::endWorldTickCallback);
        registerOnTraceCallback(InventoryPreviewSyncDataUtil::onTraceCallback);
        registerOnTraceCallback(InventoryPreviewSyncDataClientOnlyUtil::onTraceCallback);
    }

    public static void endWorldTickCallback(ClientLevel world) {
        boolean currentStatus = Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld();
        HitResult hitResult = TraceUtil.getTraceResult();
        for (TraceCallback callback : traceCallbacks) {
            callback.onTrace(hitResult, lastInventoryPreviewStatus,
                    currentStatus != lastInventoryPreviewStatus);
        }
        lastInventoryPreviewStatus = currentStatus;
    }

    public static void registerOnTraceCallback(TraceCallback callback) {
        traceCallbacks.add(callback);
    }

    @FunctionalInterface
    public interface TraceCallback {
        void onTrace(@Nullable HitResult hitResult, boolean oldStatus, boolean stateChanged);
    }
}

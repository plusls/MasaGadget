package com.plusls.MasaGadget.mixin.mod_tweak.malilib.fixGetInventoryType;

import fi.dy.masa.malilib.render.InventoryOverlay;

public final class InventoryOverlayTypeCompat {
    private InventoryOverlayTypeCompat() {
    }

    public static Object generic() {
        return InventoryOverlay.InventoryRenderType.GENERIC;
    }

    public static Object furnace() {
        return InventoryOverlay.InventoryRenderType.FURNACE;
    }
}

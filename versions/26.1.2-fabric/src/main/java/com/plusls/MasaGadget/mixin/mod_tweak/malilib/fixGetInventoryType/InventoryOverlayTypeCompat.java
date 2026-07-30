package com.plusls.MasaGadget.mixin.mod_tweak.malilib.fixGetInventoryType;

import fi.dy.masa.malilib.render.InventoryOverlayType;

public final class InventoryOverlayTypeCompat {
    private InventoryOverlayTypeCompat() {
    }

    public static Object generic() {
        return InventoryOverlayType.GENERIC;
    }

    public static Object furnace() {
        return InventoryOverlayType.FURNACE;
    }
}

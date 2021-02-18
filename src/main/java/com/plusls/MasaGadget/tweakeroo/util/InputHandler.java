package com.plusls.MasaGadget.tweakeroo.util;
import com.plusls.MasaGadget.malilib.util.InventoryOverlayRenderHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

public class InputHandler implements IMouseInputHandler{
    public static void register() {
        InputHandler handler = new InputHandler();
        InputEventHandler.getInputManager().registerMouseInputHandler(handler);
    }
    @Override
    public boolean onMouseScroll(int mouseX, int mouseY, double amount) {
        if (FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() && Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld()) {
            if (amount < 0) {
                InventoryOverlayRenderHandler.instance.addSelectedIdx(1);
            } else if (amount > 0) {
                InventoryOverlayRenderHandler.instance.addSelectedIdx(-1);
            }
        }
        return false;
    }
}

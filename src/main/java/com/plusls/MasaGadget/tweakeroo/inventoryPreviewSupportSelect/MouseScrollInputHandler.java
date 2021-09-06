package com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

public class MouseScrollInputHandler implements IMouseInputHandler {
    public static void register() {
        MouseScrollInputHandler handler = new MouseScrollInputHandler();
        InputEventHandler.getInputManager().registerMouseInputHandler(handler);
    }

    @Override
    public boolean onMouseScroll(int mouseX, int mouseY, double amount) {
        if (MasaGadgetMixinPlugin.isTweakerooLoaded && Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_SELECT.getBooleanValue() &&
                FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() &&
                Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld()) {
            if (amount < 0) {
                InventoryOverlayRenderHandler.instance.addSelectedIdx(1);
            } else if (amount > 0) {
                InventoryOverlayRenderHandler.instance.addSelectedIdx(-1);
            }
        }
        return false;
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int eventButton, boolean eventButtonState) {
        // 左右中 -> 0 1 2 以此类推
        if (MasaGadgetMixinPlugin.isTweakerooLoaded && Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_SELECT.getBooleanValue() &&
                FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() &&
                Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld()
                && eventButton == 2 && eventButtonState) {
            // 按下中键决定显示容器详细信息，再按一次取消
            InventoryOverlayRenderHandler.instance.switchSelectInventory();
        }
        return false;
    }
}

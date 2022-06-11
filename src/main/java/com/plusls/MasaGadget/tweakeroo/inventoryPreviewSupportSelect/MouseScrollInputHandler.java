package com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.HitResultUtil;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import top.hendrixshen.magiclib.util.FabricUtil;

public class MouseScrollInputHandler implements IMouseInputHandler {
    public static void register() {
        MouseScrollInputHandler handler = new MouseScrollInputHandler();
        InputEventHandler.getInputManager().registerMouseInputHandler(handler);
    }

    @Override
    public boolean onMouseScroll(int mouseX, int mouseY, double amount) {
        Player player = Minecraft.getInstance().player;
        if (FabricUtil.isModLoaded(ModInfo.TWEAKEROO_MOD_ID) &&
                Configs.inventoryPreviewSupportSelect &&
                FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() &&
                HitResultUtil.getLastInventoryPreviewStatus()) {
            if (amount < 0) {
                InventoryOverlayRenderHandler.instance.addSelectedIdx(1);
            } else if (amount > 0) {
                InventoryOverlayRenderHandler.instance.addSelectedIdx(-1);
            }
            if (FabricUtil.isModLoaded(ModInfo.LITEMATICA_MOD_ID) &&
                    fi.dy.masa.litematica.config.Configs.Generic.TOOL_ITEM_ENABLED.getBooleanValue() &&
                    player != null && Registry.ITEM.getKey(player.getMainHandItem().getItem()).toString().contains(fi.dy.masa.litematica.config.Configs.Generic.TOOL_ITEM.getStringValue())) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int eventButton, boolean eventButtonState) {
        // 左右中 -> 0 1 2 以此类推
        if (FabricUtil.isModLoaded(ModInfo.TWEAKEROO_MOD_ID) &&
                Configs.inventoryPreviewSupportSelect &&
                FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() &&
                Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld()
                && eventButton == 2 && eventButtonState) {
            // 按下中键决定显示容器详细信息，再按一次取消
            InventoryOverlayRenderHandler.instance.switchSelectInventory();
        }
        return false;
    }
}

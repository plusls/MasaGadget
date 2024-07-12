package com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import top.hendrixshen.magiclib.MagicLib;

//#if MC > 11902
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//#else
import net.minecraft.core.Registry;
//#endif

public class MouseScrollInputHandler implements IMouseInputHandler {
    @Getter
    private static final MouseScrollInputHandler instance = new MouseScrollInputHandler();

    @ApiStatus.Internal
    public void init() {
        InputEventHandler.getInputManager().registerMouseInputHandler(instance);
    }

    @Override
    public boolean onMouseScroll(int mouseX, int mouseY, double amount) {
        Player player = Minecraft.getInstance().player;

        if (!MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.tweakeroo) ||
                !Configs.inventoryPreviewSupportSelect.getBooleanValue() ||
                !FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() ||
                !HitResultHandler.getInstance().getLastInventoryPreviewStatus()) {
            return false;
        }

        if (amount < 0) {
            InventoryOverlayRenderHandler.getInstance().scrollerUp();
        } else if (amount > 0) {
            InventoryOverlayRenderHandler.getInstance().scrollerDown();
        }

        return !MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.litematica) ||
                !fi.dy.masa.litematica.config.Configs.Generic.TOOL_ITEM_ENABLED.getBooleanValue() ||
                player == null ||
                //#if MC > 11902
                //$$ !BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString()
                //$$         .contains(fi.dy.masa.litematica.config.Configs.Generic.TOOL_ITEM.getStringValue());
                //#else
                !Registry.ITEM.getKey(player.getMainHandItem().getItem()).toString()
                        .contains(fi.dy.masa.litematica.config.Configs.Generic.TOOL_ITEM.getStringValue());
        //#endif
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int eventButton, boolean eventButtonState) {
        if (MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.tweakeroo) &&
                Configs.inventoryPreviewSupportSelect.getBooleanValue() &&
                FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() &&
                Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld() &&
                eventButton == 2 &&
                eventButtonState) {
            InventoryOverlayRenderHandler.getInstance().switchSelectInventory();
        }

        return false;
    }
}

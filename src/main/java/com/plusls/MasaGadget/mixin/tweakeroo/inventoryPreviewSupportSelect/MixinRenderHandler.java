package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = RenderHandler.class, remap = false)
public abstract class MixinRenderHandler {
    @ModifyVariable(method = "onRenderGameOverlayPost",
            at = @At(value = "RETURN"))
    private Minecraft checkInventoryPreviewPress(Minecraft mc) {
        if (!FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() || !Configs.inventoryPreviewSupportSelect) {
            return mc;
        }
        IKeybind iKeybind = Hotkeys.INVENTORY_PREVIEW.getKeybind();
        if (!iKeybind.isKeybindHeld()) {
            // 重置预览选择槽
            InventoryOverlayRenderHandler.instance.resetSelectedIdx();
        }
        return mc;
    }

}

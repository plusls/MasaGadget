package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = InventoryOverlay.class, remap = false)
public class MixinInventoryOverlay {
    @Inject(method = "renderStackAt", at = @At(value = "RETURN"))
    private static void addStackToolTip(ItemStack stack, float x, float y, float scale, MinecraftClient mc, CallbackInfo ci) {
        if (Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_SELECT.getBooleanValue()) {
            InventoryOverlayRenderHandler.instance.updateState((int) x, (int) y, stack);
        }
    }
}

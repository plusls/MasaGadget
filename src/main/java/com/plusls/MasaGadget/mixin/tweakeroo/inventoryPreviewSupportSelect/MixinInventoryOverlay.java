package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = InventoryOverlay.class, remap = false)
public class MixinInventoryOverlay {
    @Inject(method = "renderStackAt", at = @At(value = "RETURN"))
    private static void addStackToolTip(ItemStack stack, float x, float y, float scale, Minecraft mc, CallbackInfo ci) {
        if (Configs.inventoryPreviewSupportSelect) {
            InventoryOverlayRenderHandler.instance.updateState((int) x, (int) y, stack);
        }
    }
}

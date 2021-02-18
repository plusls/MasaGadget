package com.plusls.MasaGadget.mixin.malilib.feature.renderStackToolTip;

import com.plusls.MasaGadget.malilib.util.InventoryOverlayRenderHandler;
import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryOverlay.class, remap = false)
public class MixinInventoryOverlay {
    @Inject(method = "renderStackAt", at = @At(value = "RETURN"))
    private static void addStackToolTip(ItemStack stack, float x, float y, float scale, MinecraftClient mc, CallbackInfo ci) {
        InventoryOverlayRenderHandler.instance.updateState((int) x, (int) y, stack);
    }
    @Inject(method = "renderInventoryStacks", at = @At(value = "RETURN"))
    private static void renderToolTip0(InventoryOverlay.InventoryRenderType type, Inventory inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, MinecraftClient mc, CallbackInfo ci) {
        InventoryOverlayRenderHandler.instance.render();
    }
    @Inject(method = "renderEquipmentStacks", at = @At(value = "RETURN"))
    private static void renderToolTi1p(LivingEntity entity, int x, int y, MinecraftClient mc, CallbackInfo ci) {
        InventoryOverlayRenderHandler.instance.render();
    }
}

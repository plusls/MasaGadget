package com.plusls.MasaGadget.mixin.malilib.fixGetInventoryType;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.NeedObfuscate;
import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@NeedObfuscate(packageName = "com.plusls.MasaGadget.mixin")
@Mixin(value = InventoryOverlay.class, remap = false)
public class MixinInventoryOverlay {

    @Inject(method = "getInventoryType(Lnet/minecraft/inventory/Inventory;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            at = @At(value = "RETURN"), cancellable = true)
    private static void checkAbstractFurnaceBlockEntity(Inventory inv, CallbackInfoReturnable<InventoryOverlay.InventoryRenderType> cir) {
        if (Configs.Malilib.FIX_GET_INVENTORY_TYPE.getBooleanValue() &&
                cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC && inv instanceof AbstractFurnaceBlockEntity) {
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
        }
    }

    @Inject(method = "getInventoryType(Lnet/minecraft/item/ItemStack;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            at = @At(value = "RETURN"), cancellable = true)
    private static void checkAbstractFurnaceBlockEntity(ItemStack stack, CallbackInfoReturnable<InventoryOverlay.InventoryRenderType> cir) {
        Item item = stack.getItem();
        if (Configs.Malilib.FIX_GET_INVENTORY_TYPE.getBooleanValue() &&
                cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC &&
                item instanceof BlockItem &&
                ((BlockItem) item).getBlock() instanceof AbstractFurnaceBlock) {
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
        }
    }
}

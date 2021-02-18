package com.plusls.MasaGadget.mixin.malilib.fix.getInventoryType;

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

@Mixin(value = InventoryOverlay.class, remap = false)
public class MixinInventoryOverlay {
    // CLASS net/minecraft/class_1263 net/minecraft/inventory/Inventory
    @Inject(method = "getInventoryType(Lnet/minecraft/class_1263;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
    //@Inject(method = "getInventoryType(Lnet/minecraft/inventory/Inventory;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            at = @At(value = "RETURN"), cancellable = true)
    private static void checkAbstractFurnaceBlockEntity(Inventory inv, CallbackInfoReturnable<InventoryOverlay.InventoryRenderType> cir) {
        if (cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC && inv instanceof AbstractFurnaceBlockEntity) {
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
        }
    }

    // CLASS net/minecraft/class_1799 net/minecraft/item/ItemStack
    @Inject(method = "getInventoryType(Lnet/minecraft/class_1799;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
    // @Inject(method = "getInventoryType(Lnet/minecraft/item/ItemStack;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            at = @At(value = "RETURN"), cancellable = true)
    private static void checkAbstractFurnaceBlockEntity(ItemStack stack, CallbackInfoReturnable<InventoryOverlay.InventoryRenderType> cir) {
        Item item = stack.getItem();
        if (cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC &&
                item instanceof BlockItem &&
                ((BlockItem) item).getBlock() instanceof AbstractFurnaceBlock) {
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
        }
    }
}

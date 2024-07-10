package com.plusls.MasaGadget.mixin.mod_tweak.malilib.fixGetInventoryType;

import com.plusls.MasaGadget.game.Configs;
import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = "<=1.17.1"))
@Mixin(value = InventoryOverlay.class, remap = false)
public class MixinInventoryOverlay {
    @Inject(
            method = "getInventoryType(Lnet/minecraft/world/Container;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true,
            remap = true
    )
    private static void checkAbstractFurnaceBlockEntity(Container inv, CallbackInfoReturnable<InventoryOverlay.InventoryRenderType> cir) {
        if (Configs.fixGetInventoryType.getBooleanValue() &&
                cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC &&
                inv instanceof AbstractFurnaceBlockEntity) {
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
        }
    }

    @Inject(
            method = "getInventoryType(Lnet/minecraft/world/item/ItemStack;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true,
            remap = true
    )
    private static void checkAbstractFurnaceBlockEntity(@NotNull ItemStack stack, CallbackInfoReturnable<InventoryOverlay.InventoryRenderType> cir) {
        Item item = stack.getItem();

        if (Configs.fixGetInventoryType.getBooleanValue() &&
                cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC &&
                item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractFurnaceBlock) {
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
        }
    }
}

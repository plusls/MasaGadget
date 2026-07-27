package com.plusls.MasaGadget.mixin.mod_tweak.malilib.fixGetInventoryType;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.render.InventoryOverlay;
//#if MC >= 26.1
//$$ import fi.dy.masa.malilib.render.InventoryOverlayType;
//#endif
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.api.dependency.DependencyType;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.platform.PlatformType;

import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc1.18: subproject 1.16.5 (main project)        &lt;--------</li>
 * <li>mc1.19+        : subproject 1.19.2 [dummy]</li>
 */
// CHECKSTYLE.ON: JavadocStyle
@Dependencies(
        require = {
                @Dependency(value = ModId.malilib, versionPredicates = "<0.11.0"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FABRIC_LIKE)
        }
)
@Dependencies(
        require = {
                @Dependency(value = ModId.minecraft, versionPredicates = "<1.18-"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FORGE_LIKE)
        }
)
@Mixin(value = InventoryOverlay.class, remap = false)
public abstract class MixinInventoryOverlay {
    @Inject(
            //#if MC >= 26.1
            //$$ method = "getInventoryType(Lnet/minecraft/world/Container;)Lfi/dy/masa/malilib/render/InventoryOverlayType;",
            //#else
            method = "getInventoryType(Lnet/minecraft/world/Container;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            //#endif
            at = @At("RETURN"),
            cancellable = true,
            remap = true
    )
    private static void checkAbstractFurnaceBlockEntity(Container inv, CallbackInfoReturnable<
            //#if MC >= 26.1
            //$$ InventoryOverlayType
            //#else
            InventoryOverlay.InventoryRenderType
            //#endif
            > cir) {
        if (Configs.fixGetInventoryType.getBooleanValue()
                //#if MC >= 26.1
                //$$ && cir.getReturnValue() == InventoryOverlayType.GENERIC
                //#else
                && cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC
                //#endif
                && inv instanceof AbstractFurnaceBlockEntity) {
            //#if MC >= 26.1
            //$$ cir.setReturnValue(InventoryOverlayType.FURNACE);
            //#else
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
            //#endif
        }
    }

    @Inject(
            //#if MC >= 26.1
            //$$ method = "getInventoryType(Lnet/minecraft/world/item/ItemStack;)Lfi/dy/masa/malilib/render/InventoryOverlayType;",
            //#else
            method = "getInventoryType(Lnet/minecraft/world/item/ItemStack;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            //#endif
            at = @At("RETURN"),
            cancellable = true,
            remap = true
    )
    private static void checkAbstractFurnaceBlockEntity(@NotNull ItemStack stack, CallbackInfoReturnable<
            //#if MC >= 26.1
            //$$ InventoryOverlayType
            //#else
            InventoryOverlay.InventoryRenderType
            //#endif
            > cir) {
        Item item = stack.getItem();

        if (Configs.fixGetInventoryType.getBooleanValue()
                //#if MC >= 26.1
                //$$ && cir.getReturnValue() == InventoryOverlayType.GENERIC
                //#else
                && cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC
                //#endif
                && item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractFurnaceBlock) {
            //#if MC >= 26.1
            //$$ cir.setReturnValue(InventoryOverlayType.FURNACE);
            //#else
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
            //#endif
        }
    }
}

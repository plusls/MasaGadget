package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewUseCache;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.HitResultUtil;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = RenderUtils.class, remap = false)
public class MixinMixinRenderUtils {
    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/tweakeroo/util/RayTraceUtils;getRayTraceFromEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Z)Lnet/minecraft/world/phys/HitResult;",
                    ordinal = 0, remap = true))
    private static HitResult getRayTraceFromEntityFromCache(Level worldIn, Entity entityIn, boolean useLiquids) {

        if (!Configs.inventoryPreviewUseCache) {
            return RayTraceUtils.getRayTraceFromEntity(worldIn, entityIn, useLiquids);
        } else {
            return HitResultUtil.getLastHitResult();
        }
    }

    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/InventoryUtils;getInventory(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/Container;",
                    ordinal = 0, remap = true))
    private static Container getInventoryFromCache(Level world, BlockPos pos) {
        if (!Configs.inventoryPreviewUseCache) {
            return InventoryUtils.getInventory(world, pos);
        } else {
            Object blockEntity = HitResultUtil.getLastHitBlockEntity();
            if (blockEntity instanceof Container) {
                return (Container) blockEntity;
            }
            return null;
        }
    }

}

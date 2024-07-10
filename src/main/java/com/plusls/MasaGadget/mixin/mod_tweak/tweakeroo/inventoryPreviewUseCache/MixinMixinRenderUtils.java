package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewUseCache;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = RenderUtils.class, remap = false)
public class MixinMixinRenderUtils {
    @Redirect(
            method = "renderInventoryOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/tweakeroo/util/RayTraceUtils;getRayTraceFromEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Z)Lnet/minecraft/world/phys/HitResult;",
                    remap = true
            )
    )
    private static HitResult getRayTraceFromEntityFromCache(Level worldIn, Entity entityIn, boolean useLiquids) {
        if (Configs.inventoryPreviewUseCache.getBooleanValue()) {
            return HitResultHandler.getInstance().getLastHitResult()
                    .orElse(BlockHitResult.miss(Vec3.ZERO, Direction.UP, BlockPos.ZERO));
        } else {
            return RayTraceUtils.getRayTraceFromEntity(worldIn, entityIn, useLiquids);
        }
    }

    @Redirect(
            method = "renderInventoryOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/InventoryUtils;getInventory(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/Container;",
                    remap = true
            )
    )
    private static Container getInventoryFromCache(Level world, BlockPos pos) {
        if (Configs.inventoryPreviewUseCache.getBooleanValue()) {
            Object blockEntity = HitResultHandler.getInstance().getLastHitBlockEntity().orElse(null);

            if (blockEntity instanceof Container) {
                return (Container) blockEntity;
            }

            return null;
        } else {
            return InventoryUtils.getInventory(world, pos);
        }
    }
}

package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportLargeBarrel;

import carpettisaddition.CarpetTISAdditionSettings;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = {@Dependency(ModInfo.TWEAKEROO_MOD_ID), @Dependency(ModInfo.CARPET_TIS_ADDITION_MOD_ID)})
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {

    @ModifyVariable(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    ordinal = 0, remap = false), ordinal = 0)
    private static Container modifyInv(Container inv) {
        Container ret = inv;
        if (Configs.inventoryPreviewSupportLargeBarrel &&
                ret instanceof BarrelBlockEntity && CarpetTISAdditionSettings.largeBarrel) {
            BarrelBlockEntity barrelBlockEntity = (BarrelBlockEntity) ret;
            Level world = barrelBlockEntity.getLevel();
            if (world == null) {
                return ret;
            }
            BlockState blockState = world.getBlockState(barrelBlockEntity.getBlockPos());
            if (!blockState.is(Blocks.BARREL)) {
                return ret;
            }
            Direction directionOpposite = blockState.getValue(BarrelBlock.FACING).getOpposite();
            BlockPos posAdj = barrelBlockEntity.getBlockPos().relative(directionOpposite);
            BlockState blockStateAdj = world.getBlockState(posAdj);
            BarrelBlockEntity blockEntityAdj = null;
            if (blockStateAdj.is(Blocks.BARREL) && blockStateAdj.getValue(BarrelBlock.FACING) == directionOpposite) {
                blockEntityAdj = (BarrelBlockEntity) world.getChunkAt(posAdj).getBlockEntity(posAdj);
            }
            if (blockEntityAdj != null) {
                if (directionOpposite.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                    ret = new CompoundContainer(barrelBlockEntity, blockEntityAdj);
                } else {
                    ret = new CompoundContainer(blockEntityAdj, barrelBlockEntity);
                }
            }
        }
        return ret;
    }
}

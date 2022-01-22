package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportLargeBarrel;

import carpettisaddition.CarpetTISAdditionSettings;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Dependencies(dependencyList = {@Dependency(modId = ModInfo.TWEAKEROO_MOD_ID, version = "*"),
        @Dependency(modId = ModInfo.CARPET_TIS_ADDITION_MOD_ID, version = "*")})
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {

    @ModifyVariable(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    ordinal = 0, remap = false), ordinal = 0)
    private static Inventory modifyInv(Inventory inv) {
        Inventory ret = inv;
        if (Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_LARGE_BARREL.getBooleanValue() &&
                ret instanceof BarrelBlockEntity && CarpetTISAdditionSettings.largeBarrel) {
            BarrelBlockEntity barrelBlockEntity = (BarrelBlockEntity) ret;
            World world = barrelBlockEntity.getWorld();
            if (world == null) {
                return ret;
            }
            BlockState blockState = world.getBlockState(barrelBlockEntity.getPos());
            if (blockState.getBlock() != Blocks.BARREL) {
                return ret;
            }
            Direction directionOpposite = blockState.get(BarrelBlock.FACING).getOpposite();
            BlockPos posAdj = barrelBlockEntity.getPos().offset(directionOpposite);
            BlockState blockStateAdj = world.getBlockState(posAdj);
            BarrelBlockEntity blockEntityAdj = null;
            if (blockStateAdj.getBlock() == Blocks.BARREL && blockStateAdj.get(BarrelBlock.FACING) == directionOpposite) {
                blockEntityAdj = (BarrelBlockEntity) world.getWorldChunk(posAdj).getBlockEntity(posAdj);
            }
            if (blockEntityAdj != null) {
                if (directionOpposite.getDirection() == Direction.AxisDirection.POSITIVE) {
                    ret = new DoubleInventory(barrelBlockEntity, blockEntityAdj);
                } else {
                    ret = new DoubleInventory(blockEntityAdj, barrelBlockEntity);
                }
            }
        }
        return ret;
    }
}

package io.github.plusls.MasaGadget.mixin.client.tweakeroo;

import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import io.github.plusls.MasaGadget.network.DataAccessor;
import net.minecraft.block.entity.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {
    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/InventoryUtils;getInventory(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Lnet/minecraft/class_1263;",
                    ordinal = 0))
    private static Inventory redirectGetBlockInventory(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getWorldChunk(pos).getBlockEntity(pos);
        if (blockEntity instanceof AbstractFurnaceBlockEntity ||
                blockEntity instanceof DispenserBlockEntity ||
                blockEntity instanceof HopperBlockEntity ||
                blockEntity instanceof ShulkerBoxBlockEntity
        ) {
            DataAccessor.requestBlockEntity(pos);
        }
        return InventoryUtils.getInventory(world, pos);
    }

    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/VillagerEntity;getInventory()Lnet/minecraft/inventory/SimpleInventory;",
                    ordinal = 0, remap = true))
    private static SimpleInventory redirectGetVillagerInventory(VillagerEntity entity) {
        DataAccessor.requestEntity(entity.getEntityId());
        return entity.getInventory();
    }
}

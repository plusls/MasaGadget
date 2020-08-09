package io.github.plusls.MasaGadget.mixin.client.malilib;

import fi.dy.masa.malilib.util.InventoryUtils;
import io.github.plusls.MasaGadget.network.DataAccessor;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryUtils.class, remap = false)
public abstract class MixinInventoryUtils {
    @Inject(method = "getInventory",
            at = @At(value = "HEAD"))
    private static void preGetInventory(World world, BlockPos pos, CallbackInfoReturnable<Inventory> info) {
        DataAccessor.requestBlockEntity(pos);
    }
}

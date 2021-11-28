package com.plusls.MasaGadget.mixin.tweakeroo.pcaSyncProtocol;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.tweakeroo.pcaSyncProtocol.PcaSyncProtocol;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("DefaultAnnotationParam")
@Dependencies(dependencyList = @Dependency(modId = ModInfo.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {

    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/InventoryUtils;getInventory(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/inventory/Inventory;",
                    ordinal = 0, remap = true))
    private static Inventory redirectGetBlockInventory(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getWorldChunk(pos).getBlockEntity(pos);
        if (Configs.Tweakeroo.PCA_SYNC_PROTOCOL.getBooleanValue() && PcaSyncProtocol.enable && (
                blockEntity instanceof AbstractFurnaceBlockEntity ||
                        blockEntity instanceof DispenserBlockEntity ||
                        blockEntity instanceof HopperBlockEntity ||
                        blockEntity instanceof ShulkerBoxBlockEntity ||
                        blockEntity instanceof BarrelBlockEntity ||
                        blockEntity instanceof BrewingStandBlockEntity ||
                        blockEntity instanceof ChestBlockEntity ||
                        (blockEntity instanceof ComparatorBlockEntity && Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_COMPARATOR.getBooleanValue()) ||
                        (blockEntity instanceof BeehiveBlockEntity && Configs.Minihud.PCA_SYNC_PROTOCOL_SYNC_BEEHIVE.getBooleanValue())
        )) {
            PcaSyncProtocol.syncBlockEntity(pos);
        }
        return InventoryUtils.getInventory(world, pos);
    }

    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;",
                    ordinal = 0, remap = true))
    private static Entity redirectGetEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (Configs.Tweakeroo.PCA_SYNC_PROTOCOL.getBooleanValue() && PcaSyncProtocol.enable) {
            if (entity instanceof Inventory || entity instanceof MerchantEntity || entity instanceof HorseBaseEntity
                    || entity instanceof PlayerEntity) {
                PcaSyncProtocol.syncEntity(entity.getEntityId());
            }
        }
        return entity;
    }
}

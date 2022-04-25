package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSyncData;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;


@SuppressWarnings("DefaultAnnotationParam")
@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {

    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/InventoryUtils;getInventory(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/Container;",
                    ordinal = 0, remap = true))
    private static Container redirectGetBlockInventory(Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getChunkAt(pos).getBlockEntity(pos);
        if (Configs.inventoryPreviewSyncData && PcaSyncProtocol.enable && (
                blockEntity instanceof AbstractFurnaceBlockEntity ||
                        blockEntity instanceof DispenserBlockEntity ||
                        blockEntity instanceof HopperBlockEntity ||
                        blockEntity instanceof ShulkerBoxBlockEntity ||
                        blockEntity instanceof BarrelBlockEntity ||
                        blockEntity instanceof BrewingStandBlockEntity ||
                        blockEntity instanceof ChestBlockEntity ||
                        (blockEntity instanceof ComparatorBlockEntity && Configs.inventoryPreviewSupportComparator) ||
                        //#if MC > 11404
                        (blockEntity instanceof BeehiveBlockEntity && Configs.pcaSyncProtocolSyncBeehive)
                //#else
                //$$ true
                //#endif
        )) {
            PcaSyncProtocol.syncBlockEntity(pos);
        }
        return InventoryUtils.getInventory(world, pos);
    }

    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/EntityHitResult;getEntity()Lnet/minecraft/world/entity/Entity;",
                    ordinal = 0, remap = true))
    private static Entity redirectGetEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (Configs.inventoryPreviewSyncData && PcaSyncProtocol.enable) {
            if (entity instanceof Container || entity instanceof AbstractVillager || entity instanceof AbstractHorse
                    || entity instanceof Player) {
                PcaSyncProtocol.syncEntity(entity.getId());
            }
        }
        return entity;
    }
}

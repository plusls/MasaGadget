package com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncData;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.HitResultUtil;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;


public class InventoryPreviewSyncDataUtil {

    public static void onHitCallback(@Nullable HitResult hitResult, boolean oldStatus, boolean stateChanged) {
        Minecraft mc = Minecraft.getInstance();
        if (!Configs.inventoryPreviewSyncData ||
                !PcaSyncProtocol.enable ||
                mc.hasSingleplayerServer() ||
                !FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue()) {
            return;
        }

        if (oldStatus) {
            if (stateChanged) {
                PcaSyncProtocol.cancelSyncBlockEntity();
                PcaSyncProtocol.cancelSyncEntity();
                return;
            }
        } else {
            return;
        }

        if (hitResult == null) {
            PcaSyncProtocol.cancelSyncBlockEntity();
            PcaSyncProtocol.cancelSyncEntity();
            return;
        }

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
            Object blockEntity = HitResultUtil.getLastHitBlockEntity();
            if (blockEntity instanceof AbstractFurnaceBlockEntity ||
                    blockEntity instanceof DispenserBlockEntity ||
                    blockEntity instanceof HopperBlockEntity ||
                    blockEntity instanceof ShulkerBoxBlockEntity ||
                    blockEntity instanceof BarrelBlockEntity ||
                    blockEntity instanceof BrewingStandBlockEntity ||
                    blockEntity instanceof ChestBlockEntity ||
                    blockEntity instanceof CompoundContainer ||
                    (blockEntity instanceof ComparatorBlockEntity && Configs.inventoryPreviewSupportComparator) ||
                    //#if MC > 11404
                    (blockEntity instanceof BeehiveBlockEntity && Configs.pcaSyncProtocolSyncBeehive)
                    //#else
                    //$$ true
                    //#endif
            ) {
                PcaSyncProtocol.syncBlockEntity(pos);
            }
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            if (entity instanceof Container ||
                    entity instanceof AbstractVillager ||
                    entity instanceof AbstractHorse ||
                    entity instanceof Player) {
                PcaSyncProtocol.syncEntity(entity.getId());
            }
        }
    }
}

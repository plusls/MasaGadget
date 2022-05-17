package com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncDataClientOnly;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.generic.cacheContainerMenu.cacheContainerMenu.CacheContainerMenuHandler;
import com.plusls.MasaGadget.util.HitResultUtil;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class InventoryPreviewSyncDataClientOnlyUtil {
    public static void onHitCallback(@Nullable HitResult hitResult, boolean oldStatus, boolean stateChanged) {
        Minecraft mc = Minecraft.getInstance();
        if (!Configs.inventoryPreviewSyncDataClientOnly ||
                (Configs.inventoryPreviewSyncData && PcaSyncProtocol.enable) ||
                mc.hasSingleplayerServer() ||
                !FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue()
        ) {
            return;
        }

        if (oldStatus) {
            if (stateChanged) {
                Objects.requireNonNull(Minecraft.getInstance().player).closeContainer();
                return;
            }
        } else {
            return;
        }

        ClientLevel world = Objects.requireNonNull(mc.level);
        if (hitResult == null) {
            Objects.requireNonNull(Minecraft.getInstance().player).closeContainer();
            return;
        }
        LocalPlayer player = Objects.requireNonNull(mc.player);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos pos = blockHitResult.getBlockPos();
            Object blockEntity = HitResultUtil.getLastHitBlockEntity();
            if (blockEntity instanceof Container && !pos.equals(CacheContainerMenuHandler.lastClickBlockPos)) {
                player.closeContainer();
                Objects.requireNonNull(mc.gameMode).useItemOn(player,
                        //#if MC <= 11802
                        world,
                        //#endif
                        InteractionHand.MAIN_HAND, blockHitResult);
            }
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            if (entity instanceof Container ||
                    entity instanceof AbstractHorse) {
                // TODO
                // PcaSyncProtocol.syncEntity(entity.getId());
            }
        }
    }
}

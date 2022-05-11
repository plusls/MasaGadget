package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSyncDataClientOnly;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.generic.cacheContainerMenu.cacheContainerMenu.CacheContainerMenuHandler;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import com.plusls.MasaGadget.util.TraceUtil;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

import java.util.Objects;


@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {

    @SuppressWarnings("DuplicatedCode")
    @ModifyVariable(method = "renderInventoryOverlay",
            at = @At(value = "HEAD"), argsOnly = true, ordinal = 0)
    private static Minecraft preOnRenderInventoryOverlay(Minecraft mc) {
        if (!Configs.inventoryPreviewSyncDataClientOnly ||
                (Configs.inventoryPreviewSyncData && PcaSyncProtocol.enable) ||
                mc.hasSingleplayerServer()) {
            return mc;
        }
        ClientLevel world = Objects.requireNonNull(mc.level);
        HitResult hitResult = TraceUtil.getTraceResult();
        if (hitResult == null) {
            return mc;
        }
        LocalPlayer player = Objects.requireNonNull(mc.player);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockEntity blockEntity = world.getChunkAt(pos).getBlockEntity(pos);
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
        return mc;

    }

}

package com.plusls.MasaGadget.util;

import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ConcurrentModificationException;


public class TraceUtil {

    @Nullable
    public static Entity getTraceEntity() {
        HitResult trace = getTraceResult();
        if (trace == null) {
            return null;
        }
        if (trace.getType() != HitResult.Type.ENTITY) {
            return null;
        }
        return ((EntityHitResult) trace).getEntity();
    }

    @Nullable
    public static HitResult getTraceResult() {
        Minecraft mc = Minecraft.getInstance();
        Level world = WorldUtils.getBestWorld(mc);
        CameraEntity cameraEntity = CameraEntity.getCamera();
        if (world == null || mc.player == null) {
            return null;
        }
        Player player = world.getPlayerByUUID(mc.player.getUUID());
        if (player == null) {
            player = mc.player;
        }
        try {
            HitResult hitResult = RayTraceUtils.getRayTraceFromEntity(world, FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && cameraEntity != null ?
                    cameraEntity : player, false);
            if (hitResult.getType() == HitResult.Type.MISS) {
                return null;
            }
            return hitResult;
        } catch (ConcurrentModificationException e) {
            // 不知道为啥，在容器预览时调用该函数有概率崩溃（在实体频繁生成死亡的时候，比如刷石机）
            return null;
        }
    }

    @Nullable
    public static BlockPos getTraceBlockPos() {
        HitResult trace = getTraceResult();
        if (trace == null) {
            return null;
        }
        if (trace.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        return ((BlockHitResult) trace).getBlockPos();
    }
}

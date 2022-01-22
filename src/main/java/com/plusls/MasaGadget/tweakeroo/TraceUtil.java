package com.plusls.MasaGadget.tweakeroo;

import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


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
        MinecraftClient mc = MinecraftClient.getInstance();
        World world = WorldUtils.getBestWorld(mc);
        if (world == null || mc.getCameraEntity() == null || mc.player == null) {
            return null;
        }
        PlayerEntity player = world.getPlayerByUuid(mc.player.getUuid());
        if (player == null) {
            player = mc.player;
        }
        return RayTraceUtils.getRayTraceFromEntity(world, FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() ? mc.getCameraEntity() : player, false);
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

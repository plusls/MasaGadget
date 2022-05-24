package com.plusls.MasaGadget.util;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncData.InventoryPreviewSyncDataUtil;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSyncDataClientOnly.InventoryPreviewSyncDataClientOnlyUtil;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.util.FabricUtil;

import javax.annotation.Nonnull;
import java.util.*;

public class HitResultUtil {

    private static final Set<HitResultCallback> hitCallbacks = new HashSet<>();
    private static boolean lastInventoryPreviewStatus = false;

    @Nullable
    private static HitResult lastHitResult = null;

    @Nullable
    private static Object lastHitBlockEntity = null;

    @Nullable
    public static Object getLastHitBlockEntity() {
        return lastHitBlockEntity;
    }

    @Nullable
    public static HitResult getLastHitResult() {
        return lastHitResult;
    }

    public static Entity getCameraEntity() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.getCameraEntity();
        if (!ModInfo.isModLoaded(ModInfo.TWEAKEROO_MOD_ID) || !FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() || entity == null) {
            entity = mc.player;
        }

        return entity;
    }

    @Nullable
    public static Entity getHitEntity() {
        HitResult hitResult = lastHitResult;
        if (hitResult == null) {
            return null;
        }
        if (hitResult.getType() != HitResult.Type.ENTITY) {
            return null;
        }
        return ((EntityHitResult) hitResult).getEntity();
    }

    @Nullable
    public static HitResult getHitResult() {
        Minecraft mc = Minecraft.getInstance();
        Level world = WorldUtils.getBestWorld(mc);

        if (world == null || mc.player == null) {
            return null;
        }
        Player player = world.getPlayerByUUID(mc.player.getUUID());
        if (player == null) {
            player = mc.player;
        }

        Player cameraEntity = (Player) getCameraEntity();
        if (cameraEntity != null) {
            player = cameraEntity;
        }

        try {
            HitResult hitResult;
            hitResult = getRayTraceFromEntity(world, player, false);
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
    public static BlockPos getHitBlockPos() {
        HitResult hitResult = lastHitResult;
        if (hitResult == null) {
            return null;
        }
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        return ((BlockHitResult) hitResult).getBlockPos();
    }


    // code from tweakeroo RayTraceUtils.getRayTraceFromEntity
    @Nonnull
    public static HitResult getRayTraceFromEntity(Level worldIn, Entity entityIn, boolean useLiquids) {
        double reach = 5.0;
        return getRayTraceFromEntity(worldIn, entityIn, useLiquids, reach);
    }

    @Nonnull
    public static HitResult getRayTraceFromEntity(Level worldIn, Entity entityIn, boolean useLiquids, double range) {
        Vec3 eyesVec = new Vec3(entityIn.getX(), entityIn.getY() + (double) entityIn.getEyeHeight(), entityIn.getZ());
        Vec3 rangedLookRot = entityIn.getViewVector(1.0F).scale(range);
        Vec3 lookVec = eyesVec.add(rangedLookRot);
        ClipContext.Fluid fluidMode = useLiquids ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE;
        ClipContext context = new ClipContext(eyesVec, lookVec, ClipContext.Block.COLLIDER, fluidMode, entityIn);
        HitResult result = worldIn.clip(context);
        if (result == null) {
            result = BlockHitResult.miss(Vec3.ZERO, Direction.UP, BlockPos.ZERO);
        }

        AABB bb = entityIn.getBoundingBox().inflate(rangedLookRot.x, rangedLookRot.y, rangedLookRot.z).inflate(1.0, 1.0, 1.0);
        List<Entity> list = worldIn.getEntities(entityIn, bb);
        double closest = result.getType() == HitResult.Type.BLOCK ? eyesVec.distanceTo(result.getLocation()) : Double.MAX_VALUE;
        Optional<Vec3> entityTrace = Optional.empty();
        Entity targetEntity = null;

        for (Entity entity : list) {
            bb = entity.getBoundingBox();
            Optional<Vec3> traceTmp = bb.clip(lookVec, eyesVec);
            if (traceTmp.isPresent()) {
                double distance = eyesVec.distanceTo(traceTmp.get());
                if (distance <= closest) {
                    targetEntity = entity;
                    entityTrace = traceTmp;
                    closest = distance;
                }
            }
        }

        if (targetEntity != null) {
            result = new EntityHitResult(targetEntity, entityTrace.get());
        }

        return result;
    }

    public static void init() {
        ClientTickEvents.END_WORLD_TICK.register(world -> HitResultUtil.endWorldTickCallback());
        if (FabricUtil.isModLoaded(ModInfo.TWEAKEROO_MOD_ID)) {
            registerOnHitCallback(InventoryPreviewSyncDataUtil::onHitCallback);
            registerOnHitCallback(InventoryPreviewSyncDataClientOnlyUtil::onHitCallback);
            registerOnHitCallback(InventoryOverlayRenderHandler::onHitCallback);
        }
    }

    public static boolean getLastInventoryPreviewStatus() {
        return lastHitResult != null && lastInventoryPreviewStatus;
    }

    public static void endWorldTickCallback() {
        Level world = Objects.requireNonNull(WorldUtils.getBestWorld(Minecraft.getInstance()));
        boolean currentStatus = false;
        if (FabricUtil.isModLoaded(ModInfo.TWEAKEROO_MOD_ID)) {
            currentStatus = Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld();
        }
        lastHitResult = getHitResult();
        lastHitBlockEntity = null;
        if (lastHitResult != null && lastHitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = getHitBlockPos();
            if (pos != null) {
                // 绕过线程检查
                lastHitBlockEntity = MiscUtil.getContainer(world, pos);
                if (lastHitBlockEntity == null) {
                    LevelChunk levelChunk = world.getChunkAt(pos);
                    if (levelChunk != null) {
                        lastHitBlockEntity = levelChunk.getBlockEntity(pos);
                    }
                }
            }
        }
        for (HitResultCallback callback : hitCallbacks) {
            callback.onHit(lastHitResult, lastInventoryPreviewStatus,
                    currentStatus != lastInventoryPreviewStatus);
        }
        lastInventoryPreviewStatus = currentStatus;
    }

    public static void registerOnHitCallback(HitResultCallback callback) {
        hitCallbacks.add(callback);
    }

    @FunctionalInterface
    public interface HitResultCallback {
        void onHit(@Nullable HitResult hitResult, boolean oldStatus, boolean stateChanged);
    }
}

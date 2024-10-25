package com.plusls.MasaGadget.impl.generic;

import com.google.common.collect.Sets;
import com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import com.plusls.MasaGadget.util.InventoryPreviewSyncDataClientOnlyUtil;
import com.plusls.MasaGadget.util.InventoryPreviewSyncDataUtil;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import lombok.Getter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.compat.minecraft.util.ProfilerCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.EntityCompat;
import top.hendrixshen.magiclib.util.collect.ValueContainer;

import java.util.*;

public class HitResultHandler {
    @Getter
    private static final HitResultHandler instance = new HitResultHandler();

    private final Set<HitResultCallback> hitCallbacks = Sets.newHashSet();
    private boolean lastInventoryPreviewStatus = false;
    @Nullable
    private HitResult lastHitResult = null;
    @Nullable
    private Object lastHitBlockEntity = null;

    @ApiStatus.Internal
    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> HitResultHandler.getInstance().endClientTickCallback());

        if (MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.tweakeroo)) {
            this.registerOnHitCallback(InventoryPreviewSyncDataUtil::onHitCallback);
            this.registerOnHitCallback(InventoryPreviewSyncDataClientOnlyUtil::onHitCallback);
            this.registerOnHitCallback(InventoryOverlayRenderHandler::onHitCallback);
        }
    }

    public ValueContainer<Object> getLastHitBlockEntity() {
        return ValueContainer.ofNullable(this.lastHitBlockEntity);
    }

    public ValueContainer<HitResult> getLastHitResult() {
        return ValueContainer.ofNullable(this.lastHitResult);
    }

    public ValueContainer<Entity> getCameraEntity() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            return null;
        }

        Player player = mc.player;
        Entity cameraEntity = mc.getCameraEntity();

        if (!MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.tweakeroo) ||
                !FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() || cameraEntity == null) {
            cameraEntity = player;
        }

        return ValueContainer.of(cameraEntity);
    }

    public ValueContainer<Entity> getHitEntity() {
        HitResult hitResult = this.lastHitResult;

        if (hitResult == null) {
            return ValueContainer.empty();
        }

        if (hitResult.getType() != HitResult.Type.ENTITY) {
            return ValueContainer.empty();
        }

        return ValueContainer.of(((EntityHitResult) hitResult).getEntity());
    }

    public ValueContainer<HitResult> getHitResult() {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;

        if (level == null) {
            return ValueContainer.empty();
        }

        ValueContainer<Entity> cameraEntity = this.getCameraEntity();

        if (cameraEntity.isEmpty()) {
            return ValueContainer.empty();
        }

        try {
            HitResult hitResult = this.getRayTraceFromEntity(level, cameraEntity.get(), false);

            if (hitResult.getType() == HitResult.Type.MISS) {
                return ValueContainer.empty();
            }

            if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                hitResult = new EntityHitResult(MiscUtil.getBestEntity(entityHitResult.getEntity()),
                        entityHitResult.getLocation());
            }

            return ValueContainer.of(hitResult);
        } catch (ConcurrentModificationException e) {
            // 不知道为啥，在容器预览时调用该函数有概率崩溃（在实体频繁生成死亡的时候，比如刷石机）
            return ValueContainer.empty();
        }
    }

    public ValueContainer<BlockPos> getHitBlockPos() {
        HitResult hitResult = this.lastHitResult;

        if (hitResult == null) {
            return ValueContainer.empty();
        }

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return ValueContainer.empty();
        }

        return ValueContainer.of(((BlockHitResult) hitResult).getBlockPos());
    }

    // code from tweakeroo RayTraceUtils.getRayTraceFromEntity
    public HitResult getRayTraceFromEntity(Level worldIn, Entity entityIn, boolean useLiquids) {
        double reach = 5.0;
        return this.getRayTraceFromEntity(worldIn, entityIn, useLiquids, reach);
    }

    public HitResult getRayTraceFromEntity(Level worldIn, Entity entityIn, boolean useLiquids, double range) {
        EntityCompat entityInCompat = EntityCompat.of(entityIn);
        Vec3 eyesVec = new Vec3(entityInCompat.getX(), entityInCompat.getY() + (double) entityIn.getEyeHeight(), entityInCompat.getZ());
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

    public boolean getLastInventoryPreviewStatus() {
        return this.lastHitResult != null && this.lastInventoryPreviewStatus;
    }

    public void endClientTickCallback() {
        ProfilerFiller profiler = ProfilerCompat.get();
        Level level = WorldUtils.getBestWorld(Minecraft.getInstance());

        if (level == null) {
            return;
        }

        boolean currentStatus = false;

        if (MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.tweakeroo)) {
            currentStatus = Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld();
        }

        profiler.push("HitResultUtil.getHitResult");
        this.lastHitResult = this.getHitResult().orElse(null);
        profiler.pop();
        this.lastHitBlockEntity = null;

        if (lastHitResult != null && lastHitResult.getType() == HitResult.Type.BLOCK && currentStatus) {
            ValueContainer<BlockPos> pos = this.getHitBlockPos();

            if (pos.isPresent()) {
                // 绕过线程检查
                profiler.push("MiscUtil.getContainer");
                this.lastHitBlockEntity = MiscUtil.getContainer(level, pos.get());
                profiler.pop();

                if (lastHitBlockEntity == null) {
                    profiler.push("world.getChunkAt");
                    LevelChunk levelChunk = level.getChunkAt(pos.get());
                    profiler.pop();

                    if (levelChunk != null) {
                        profiler.push("levelChunk.getBlockEntity");
                        lastHitBlockEntity = levelChunk.getBlockEntity(pos.get());
                        profiler.pop();
                    }
                }
            }
        }
        profiler.push("MiscUtil: run callbacks");

        for (HitResultCallback callback : hitCallbacks) {
            callback.onHit(this.lastHitResult, this.lastInventoryPreviewStatus,
                    currentStatus != this.lastInventoryPreviewStatus);
        }

        profiler.pop();
        this.lastInventoryPreviewStatus = currentStatus;
    }

    public void registerOnHitCallback(HitResultCallback callback) {
        this.hitCallbacks.add(callback);
    }

    @FunctionalInterface
    public interface HitResultCallback {
        void onHit(@Nullable HitResult hitResult, boolean oldStatus, boolean stateChanged);
    }
}

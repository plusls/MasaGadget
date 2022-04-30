package com.plusls.MasaGadget.util;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.minihud.renderer.shapes.ShapeBase;
import fi.dy.masa.minihud.renderer.shapes.ShapeDespawnSphere;
import fi.dy.masa.minihud.renderer.shapes.ShapeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.ComponentCompatApi;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.StyleCompatApi;

import java.util.Objects;

public class SearchMobSpawnPointUtil {
    @Nullable
    private static ShapeDespawnSphere getShapeDespawnSphere() {
        ShapeDespawnSphere ret = null;
        for (ShapeBase shapeBase : ShapeManager.INSTANCE.getAllShapes()) {
            if (shapeBase.isEnabled() && shapeBase instanceof ShapeDespawnSphere) {
                if (ret == null) {
                    ret = (ShapeDespawnSphere) shapeBase;
                } else {
                    Objects.requireNonNull(Minecraft.getInstance().player).displayClientMessage(
                            ComponentCompatApi.literal(ModInfo.translate("message.onlySupportOneDespawnShape"))
                                    .withStyle(ChatFormatting.RED), false);
                    return null;
                }
            }
        }
        if (ret == null) {
            Objects.requireNonNull(Minecraft.getInstance().player).displayClientMessage(
                    ComponentCompatApi.literal(ModInfo.translate("message.canNotFindDespawnShape"))
                            .withStyle(ChatFormatting.RED), false);
        }
        return ret;
    }

    public static void search() {
        ClientLevel world = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;

        if (world == null || player == null) {
            return;
        }
        ShapeDespawnSphere shapeDespawnSphere = getShapeDespawnSphere();
        if (shapeDespawnSphere == null) {
            return;
        }
        Vec3 centerPos = shapeDespawnSphere.getCenter();
        BlockPos pos = new BlockPos((int) centerPos.x, (int) centerPos.y, (int) centerPos.z);
        ModInfo.LOGGER.warn("shape: {}", shapeDespawnSphere.getCenter());
        BlockPos spawnPos = null;
        int maxX = pos.getX() + 129;
        int maxZ = pos.getZ() + 129;
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        //#if MC >= 11800
        int maxSpawnLightLevel = fi.dy.masa.minihud.config.Configs.Generic.LIGHT_LEVEL_THRESHOLD_SAFE.getIntegerValue();
        //#else
        //$$ int maxSpawnLightLevel = fi.dy.masa.minihud.config.Configs.Generic.LIGHT_LEVEL_THRESHOLD.getIntegerValue();
        //#endif
        LevelLightEngine lightingProvider = world.getChunkSource().getLightEngine();
        EntityType<?> entityType = world.getDimensionLocation().equals(new ResourceLocation("the_nether")) ? EntityType.ZOMBIFIED_PIGLIN : EntityType.CREEPER;
        EntityType<?> entityType2 = world.getDimensionLocation().equals(new ResourceLocation("the_nether")) ? null : EntityType.SPIDER;

        for (int x = pos.getX() - 129; x <= maxX; ++x) {
            for (int z = pos.getZ() - 129; z <= maxZ; ++z) {
                LevelChunk chunk = world.getChunk(x >> 4, z >> 4);
                if (chunk == null) {
                    continue;
                }
                int maxY = Math.min(pos.getY() + 129, chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1);
                for (int y = Math.max(pos.getY() - 129, world.getMinBuildHeight() + 1); y <= maxY; ++y) {
                    if (squaredDistanceTo(x, y, z, centerPos) > 16384) {
                        if (y > centerPos.y) {
                            break;
                        } else {
                            continue;
                        }
                    } else if (spawnPos != null && player.distanceToSqr(x, y, z) >
                            player.distanceToSqr(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ())) {
                        continue;
                    }
                    currentPos.set(x, y, z);
                    if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(entityType), world, currentPos, entityType) &&
                            lightingProvider.getLayerListener(LightLayer.BLOCK).getLightValue(currentPos) < maxSpawnLightLevel) {
                        Block block = world.getBlockState(currentPos.below()).getBlock();
                        String blockId = Registry.BLOCK.getKey(world.getBlockState(currentPos.below()).getBlock()).toString();
                        String blockName = block.getName().getString();
                        if (Configs.searchMobSpawnPointBlackList.stream().noneMatch(s -> blockId.contains(s) || blockName.contains(s))) {
                            if (world.noCollision(entityType.getAABB(currentPos.getX() + 0.5D, currentPos.getY(), currentPos.getZ() + 0.5D))) {
                                spawnPos = currentPos.immutable();
                            } else if (entityType2 != null && world.noCollision(entityType2.getAABB(currentPos.getX() + 0.5D, currentPos.getY(), currentPos.getZ() + 0.5D))) {
                                spawnPos = currentPos.immutable();
                            }
                        }
                    }
                }
            }
        }
        Component text;
        if (spawnPos == null) {
            text = ComponentCompatApi.literal(ModInfo.translate("message.noBlockCanSpawn"))
                    .withStyle(StyleCompatApi.empty().withColor(ChatFormatting.GREEN));
        } else {
            // for ommc parser
            text = ComponentCompatApi.literal(ModInfo.translate("message.spawnPos", spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));
            player.chat(String.format("/highlightWaypoint %d %d %d", spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));
        }
        Objects.requireNonNull(Minecraft.getInstance().player).displayClientMessage(text, false);
    }

    private static double squaredDistanceTo(int x, int y, int z, Vec3 vec3d) {
        double d = vec3d.x - x;
        double e = vec3d.y - y;
        double f = vec3d.z - z;
        return d * d + e * e + f * f;
    }
}

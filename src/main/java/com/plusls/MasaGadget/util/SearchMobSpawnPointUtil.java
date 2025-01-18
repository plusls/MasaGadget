package com.plusls.MasaGadget.util;

import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.game.Configs;
import fi.dy.masa.minihud.renderer.shapes.ShapeBase;
import fi.dy.masa.minihud.renderer.shapes.ShapeDespawnSphere;
import fi.dy.masa.minihud.renderer.shapes.ShapeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.compat.minecraft.network.chat.ComponentCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.network.chat.MutableComponentCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.level.LevelCompat;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;
import top.hendrixshen.magiclib.util.minecraft.InfoUtil;

//#if MC < 12005
import net.minecraft.world.level.NaturalSpawner;
//#endif

//#if MC > 11902
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//#else
import net.minecraft.core.Registry;
//#endif

public class SearchMobSpawnPointUtil {
    @Nullable
    private static ShapeDespawnSphere getShapeDespawnSphere() {
        ShapeDespawnSphere ret = null;

        for (ShapeBase shapeBase : ShapeManager.INSTANCE.getAllShapes()) {
            if (shapeBase.isEnabled() && shapeBase instanceof ShapeDespawnSphere) {
                if (ret == null) {
                    ret = (ShapeDespawnSphere) shapeBase;
                } else {
                    InfoUtil.displayChatMessage(ComponentUtil.trCompat("masa_gadget_mod.message.only_support_one_despawn_shape")
                            .withStyle(ChatFormatting.RED));
                    return null;
                }
            }
        }

        if (ret == null) {
            InfoUtil.displayChatMessage(ComponentUtil.trCompat("masa_gadget_mod.message.can_not_find_respawn_shape")
                    .withStyle(ChatFormatting.RED));
        }

        return ret;
    }

    public static void search() {
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;

        if (level == null || player == null) {
            return;
        }

        LevelCompat levelCompat = LevelCompat.of(level);
        ShapeDespawnSphere shapeDespawnSphere = SearchMobSpawnPointUtil.getShapeDespawnSphere();

        if (shapeDespawnSphere == null) {
            return;
        }

        Vec3 centerPos = shapeDespawnSphere.getCenter();
        BlockPos pos = new BlockPos((int) centerPos.x, (int) centerPos.y, (int) centerPos.z);
        SharedConstants.getLogger().warn("shape: {}", shapeDespawnSphere.getCenter());
        BlockPos spawnPos = null;
        int maxX = pos.getX() + 129;
        int maxZ = pos.getZ() + 129;
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        //#if MC > 11701
        //$$ int maxSpawnLightLevel = fi.dy.masa.minihud.config.Configs.Generic.LIGHT_LEVEL_THRESHOLD_SAFE.getIntegerValue();
        //#else
        int maxSpawnLightLevel = fi.dy.masa.minihud.config.Configs.Generic.LIGHT_LEVEL_THRESHOLD.getIntegerValue();
        //#endif
        LevelLightEngine lightingProvider = level.getChunkSource().getLightEngine();
        EntityType<?> entityType = levelCompat.getDimensionLocation().equals(ResourceLocationCompat.withDefaultNamespace("the_nether")) ? EntityType.ZOMBIFIED_PIGLIN : EntityType.CREEPER;
        EntityType<?> entityType2 = levelCompat.getDimensionLocation().equals(ResourceLocationCompat.withDefaultNamespace("the_nether")) ? null : EntityType.SPIDER;

        for (int x = pos.getX() - 129; x <= maxX; ++x) {
            for (int z = pos.getZ() - 129; z <= maxZ; ++z) {
                LevelChunk chunk = level.getChunk(x >> 4, z >> 4);

                if (chunk == null) {
                    continue;
                }

                int maxY = Math.min(pos.getY() + 129, chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1);

                for (int y = Math.max(pos.getY() - 129, levelCompat.getMinBuildHeight() + 1); y <= maxY; ++y) {
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

                    if (
                        //#if MC > 12004
                        //$$ SpawnPlacements.isSpawnPositionOk(entityType, level, currentPos) &&
                        //#else
                            NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(entityType), level, currentPos, entityType) &&
                                    //#endif
                                    lightingProvider.getLayerListener(LightLayer.BLOCK).getLightValue(currentPos) < maxSpawnLightLevel) {
                        Block block = level.getBlockState(currentPos.below()).getBlock();
                        //#if MC > 11902
                        //$$ String blockId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(currentPos.below()).getBlock()).toString();
                        //#else
                        String blockId = Registry.BLOCK.getKey(level.getBlockState(currentPos.below()).getBlock()).toString();
                        //#endif
                        String blockName = block.getName().getString();

                        if (Configs.searchMobSpawnPointBlackList.getStrings().stream().noneMatch(s -> blockId.contains(s) || blockName.contains(s))) {
                            if (level.noCollision(
                                    //#if MC > 12004
                                    //$$ entityType.getSpawnAABB(currentPos.getX() + 0.5D, currentPos.getY(), currentPos.getZ() + 0.5D)
                                    //#else
                                    entityType.getAABB(currentPos.getX() + 0.5D, currentPos.getY(), currentPos.getZ() + 0.5D)
                                    //#endif
                            )) {
                                spawnPos = currentPos.immutable();
                            } else if (entityType2 != null && level.noCollision(
                                    //#if MC > 12004
                                    //$$ entityType.getSpawnAABB(currentPos.getX() + 0.5D, currentPos.getY(), currentPos.getZ() + 0.5D)
                                    //#else
                                    entityType2.getAABB(currentPos.getX() + 0.5D, currentPos.getY(), currentPos.getZ() + 0.5D)
                                    //#endif
                            )) {
                                spawnPos = currentPos.immutable();
                            }
                        }
                    }
                }
            }
        }

        MutableComponentCompat text;

        if (spawnPos == null) {
            text = ComponentUtil.trCompat("masa_gadget_mod.message.no_block_can_spawn")
                    .withStyle(ChatFormatting.GREEN);
        } else {
            // for ommc parser
            text = ComponentUtil.trCompat("masa_gadget_mod.message.spawn_pos", spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

            if (MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.oh_my_minecraft_client)) {
                InfoUtil.sendCommand(String.format("highlightWaypoint %d %d %d", spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));
            }
        }

        InfoUtil.displayChatMessage(text);
    }

    private static double squaredDistanceTo(int x, int y, int z, Vec3 vec3d) {
        double d = vec3d.x - x;
        double e = vec3d.y - y;
        double f = vec3d.z - z;
        return d * d + e * e + f * f;
    }
}

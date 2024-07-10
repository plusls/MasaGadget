package com.plusls.MasaGadget.util;

import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.api.i18n.I18n;

public class MiscUtil extends top.hendrixshen.magiclib.util.MiscUtil {
    public static <T extends Entity> T getBestEntity(T entity) {
        // Only try to fetch the corresponding server world if the entity is in the actual client world.
        // Otherwise the entity may be for example in Litematica's schematic world.
        Level world = entity.getCommandSenderWorld();
        Minecraft client = Minecraft.getInstance();
        T ret = entity;
        if (world == client.level) {
            world = WorldUtils.getBestWorld(client);
            if (world != null && world != client.level) {
                Entity bestEntity = world.getEntity(entity.getId());
                if (entity.getClass().isInstance(bestEntity)) {
                    ret = MiscUtil.cast(bestEntity);
                }
            }
        }
        return ret;
    }

    public static String getStringWithoutFormat(String text) {
        StringBuilder ret = new StringBuilder(text);
        if (text.contains("§")) {
            ret = new StringBuilder();
            for (int i = 0; i < text.length(); ++i) {
                if (text.charAt(i) == '§') {
                    ++i;
                    continue;
                }
                ret.append(text.charAt(i));
            }
        }
        return ret.toString();
    }

    @Nullable
    public static String getTranslatedOrFallback(String key, @Nullable String fallback, Object... objects) {
        String translated = I18n.tr(key, objects);
        return !key.equals(translated) ? translated : fallback;
    }

    // Only call in main thread!
    @Nullable
    public static Container getContainer(@NotNull Level level, BlockPos pos) {
        LevelChunk levelChunk = level.getChunkAt(pos);
        if (levelChunk == null) {
            return null;
        } else {
            BlockEntity blockEntity = levelChunk.getBlockEntity(pos);
            if (blockEntity instanceof Container) {
                Container container = (Container) blockEntity;
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof ChestBlock && blockEntity instanceof ChestBlockEntity) {
                    ChestType type = state.getValue(ChestBlock.TYPE);
                    if (type != ChestType.SINGLE) {
                        BlockPos posAdj = pos.relative(ChestBlock.getConnectedDirection(state));
                        LevelChunk levelChunkAdj = level.getChunkAt(posAdj);
                        if (levelChunkAdj != null) {
                            BlockState stateAdj = level.getBlockState(posAdj);
                            BlockEntity te2 = levelChunkAdj.getBlockEntity(posAdj);
                            if (stateAdj.getBlock() == state.getBlock()
                                    && te2 instanceof ChestBlockEntity
                                    && stateAdj.getValue(ChestBlock.TYPE) != ChestType.SINGLE
                                    && stateAdj.getValue(ChestBlock.FACING) == state.getValue(ChestBlock.FACING)) {
                                Container invRight = type == ChestType.RIGHT ? container : (Container) te2;
                                Container invLeft = type == ChestType.RIGHT ? (Container) te2 : container;
                                container = new CompoundContainer(invRight, invLeft);
                            }
                        }
                    }
                }
                return container;
            } else {
                return null;
            }
        }
    }
}

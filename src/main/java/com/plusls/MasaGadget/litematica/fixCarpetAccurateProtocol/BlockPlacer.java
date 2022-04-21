package com.plusls.MasaGadget.litematica.fixCarpetAccurateProtocol;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

// from https://github.com/gnembon/carpet-extra/blob/master/src/main/java/carpetextra/utils/BlockPlacer.java
public class BlockPlacer {
    public static BlockState alternativeBlockPlacement(Block block, BlockPlaceContext context)//World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        //actual alternative block placement code

        Direction facing;
        Vec3 vec3d = context.getClickLocation();
        BlockPos pos = context.getClickedPos();
        double hitX = vec3d.x - pos.getX();
        if (hitX < 2) // vanilla
            return null;
        int code = (int) (hitX - 2) / 2;
        //
        // now it would be great if hitX was adjusted in context to original range from 0.0 to 1.0
        // since its actually using it. Its private - maybe with Reflections?
        //
        Player placer = Objects.requireNonNull(context.getPlayer());
        Level world = context.getLevel();

        if (block instanceof GlazedTerracottaBlock) {
            facing = Direction.from3DDataValue(code);
            if (facing == Direction.UP || facing == Direction.DOWN) {
                facing = placer.getDirection().getOpposite();
            }
            return block.defaultBlockState().setValue(GlazedTerracottaBlock.FACING, facing);
        } else if (block instanceof ObserverBlock) {
            return block.defaultBlockState()
                    .setValue(ObserverBlock.FACING, Direction.from3DDataValue(code))
                    .setValue(ObserverBlock.POWERED, true);
        } else if (block instanceof RepeaterBlock) {
            facing = Direction.from3DDataValue(code % 16);
            if (facing == Direction.UP || facing == Direction.DOWN) {
                facing = placer.getDirection().getOpposite();
            }
            return block.defaultBlockState()
                    .setValue(RepeaterBlock.FACING, facing)
                    .setValue(RepeaterBlock.DELAY, Mth.clamp(code / 16, 1, 4))
                    .setValue(RepeaterBlock.LOCKED, Boolean.FALSE);
        } else if (block instanceof TrapDoorBlock) {
            facing = Direction.from3DDataValue(code % 16);
            if (facing == Direction.UP || facing == Direction.DOWN) {
                facing = placer.getDirection().getOpposite();
            }
            return block.defaultBlockState()
                    .setValue(TrapDoorBlock.FACING, facing)
                    .setValue(TrapDoorBlock.OPEN, Boolean.FALSE)
                    .setValue(TrapDoorBlock.HALF, (code >= 16) ? Half.TOP : Half.BOTTOM)
                    .setValue(TrapDoorBlock.OPEN, world.hasNeighborSignal(pos));
        } else if (block instanceof ComparatorBlock) {
            facing = Direction.from3DDataValue(code % 16);
            if ((facing == Direction.UP) || (facing == Direction.DOWN)) {
                facing = placer.getDirection().getOpposite();
            }
            ComparatorMode m = (hitX >= 16) ? ComparatorMode.SUBTRACT : ComparatorMode.COMPARE;
            return block.defaultBlockState()
                    .setValue(ComparatorBlock.FACING, facing)
                    .setValue(ComparatorBlock.POWERED, Boolean.FALSE)
                    .setValue(ComparatorBlock.MODE, m);
        } else if (block instanceof DispenserBlock) {
            return block.defaultBlockState()
                    .setValue(DispenserBlock.FACING, Direction.from3DDataValue(code))
                    .setValue(DispenserBlock.TRIGGERED, Boolean.FALSE);
        } else if (block instanceof PistonBaseBlock) {
            return block.defaultBlockState()
                    .setValue(DirectionalBlock.FACING, Direction.from3DDataValue(code))
                    .setValue(PistonBaseBlock.EXTENDED, Boolean.FALSE);
        } else if (block instanceof StairBlock) {
            return Objects.requireNonNull(block.getStateForPlacement(context))//worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
                    .setValue(StairBlock.FACING, Direction.from3DDataValue(code % 16))
                    .setValue(StairBlock.HALF, (hitX >= 16) ? Half.TOP : Half.BOTTOM);
        }
        return null;
    }
}

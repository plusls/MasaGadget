package com.plusls.MasaGadget.mixin.mod_tweak.litematica.fixCarpetAccurateProtocol;

import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.game.Configs;
import fi.dy.masa.litematica.util.EasyPlaceUtils;
import fi.dy.masa.malilib.util.game.BlockUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc26.1: subproject 1.16.5 (main project) [dummy]</li>
 * <li>mc26.2+        : subproject 26.2        &lt;--------</li>
 */
// CHECKSTYLE.ON: JavadocStyle
@Mixin(value = EasyPlaceUtils.class, priority = 900, remap = false)
public abstract class MixinEasyPlaceUtils {
    @Inject(method = "applyCarpetProtocolHitVec", at = @At("HEAD"), cancellable = true)
    private static void preApplyCarpetProtocolHitVec(BlockPos pos, BlockState state, Vec3 hitVecIn, CallbackInfoReturnable<Vec3> cir) {
        if (!Configs.fixAccurateProtocol.getBooleanValue()) {
            return;
        }

        double x = hitVecIn.x;
        double y = hitVecIn.y;
        double z = hitVecIn.z;
        Block block = state.getBlock();
        Direction facing = BlockUtils.getFirstPropertyFacingValue(state).orElse(null);
        final int propertyIncrement = 32;
        double relX = hitVecIn.x - pos.getX();

        if (facing != null) {
            x = pos.getX() + relX + 2 + (facing.get3DDataValue() * 2);
        }

        if (block instanceof RepeaterBlock) {
            x += ((state.getValue(RepeaterBlock.DELAY))) * propertyIncrement;
        } else if (block instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {
            x += propertyIncrement;
        } else if (block instanceof ComparatorBlock && state.getValue(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT) {
            x += propertyIncrement;
        } else if (block instanceof StairBlock && state.getValue(StairBlock.HALF) == Half.TOP) {
            x += propertyIncrement;
        } else if (block instanceof SlabBlock && state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE) {
            //x += 10; // Doesn't actually exist (yet?)

            if (state.getValue(SlabBlock.TYPE) == SlabType.TOP) {
                y = pos.getY() + 0.9;
            } else {
                y = pos.getY();
            }
        }

        SharedConstants.getLogger().debug("applyCarpetProtocolHitVec: {} -> {}", hitVecIn, new Vec3(x, y, z).toString());
        cir.setReturnValue(new Vec3(x, y, z));
    }
}

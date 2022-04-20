package com.plusls.MasaGadget.mixin.litematica.betterEasyPlaceMode;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.litematica.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.LITEMATICA_MOD_ID))
@Mixin(value = WorldUtils.class, remap = false)
public class MixinWorldUtils {
    @Inject(method = "handleEasyPlace", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/util/InfoUtils;showGuiOrInGameMessage(Lfi/dy/masa/malilib/gui/Message$MessageType;Ljava/lang/String;[Ljava/lang/Object;)V", ordinal = 0), cancellable = true)
    private static void checkInventory(Minecraft mc, CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.betterEasyPlaceMode || mc.level == null) {
            return;
        }
        HitResult trace = mc.hitResult;
        if (trace != null && trace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) trace;
            BlockPos pos = blockHitResult.getBlockPos();
            Block block = mc.level.getBlockState(pos).getBlock();
            mc.level.getBlockState(pos);
            if (block == Blocks.BEACON || mc.level.getBlockEntity(pos) instanceof Container) {
                cir.setReturnValue(false);
            }
        }
    }
}

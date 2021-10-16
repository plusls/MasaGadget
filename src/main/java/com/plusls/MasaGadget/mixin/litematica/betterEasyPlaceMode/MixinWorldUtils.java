package com.plusls.MasaGadget.mixin.litematica.betterEasyPlaceMode;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import fi.dy.masa.litematica.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.LITEMATICA_MOD_ID, version = "*"))
@Mixin(value = WorldUtils.class, remap = false)
public class MixinWorldUtils {
    @Inject(method = "handleEasyPlace", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/util/InfoUtils;showGuiOrInGameMessage(Lfi/dy/masa/malilib/gui/Message$MessageType;Ljava/lang/String;[Ljava/lang/Object;)V", ordinal = 0), cancellable = true)
    private static void checkInventory(MinecraftClient mc, CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.Litematica.BETTER_EASY_PLACE_MODE.getBooleanValue() || mc.world == null) {
            return;
        }
        HitResult trace = mc.crosshairTarget;
        if (trace != null && trace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) trace;
            BlockPos pos = blockHitResult.getBlockPos();
            Block block = mc.world.getBlockState(pos).getBlock();
            if (block == Blocks.BEACON || mc.world.getBlockEntity(pos) instanceof Inventory) {
                cir.setReturnValue(false);
            }
        }
    }
}

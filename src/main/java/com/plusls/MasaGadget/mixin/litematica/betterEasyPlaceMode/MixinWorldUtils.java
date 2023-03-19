package com.plusls.MasaGadget.mixin.litematica.betterEasyPlaceMode;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.HitResultUtil;
import fi.dy.masa.litematica.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.LITEMATICA_MOD_ID))
@Mixin(value = WorldUtils.class, remap = false)
public class MixinWorldUtils {
    @ModifyVariable(method = "handleEasyPlace",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lfi/dy/masa/litematica/util/WorldUtils;doEasyPlaceAction(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/world/InteractionResult;",
                    ordinal = 0, remap = true),
            ordinal = 0)
    private static InteractionResult checkInventory(InteractionResult interactionResult) {
        Minecraft mc = Minecraft.getInstance();
        if (!Configs.betterEasyPlaceMode || mc.level == null || interactionResult != InteractionResult.FAIL) {
            return interactionResult;
        }
        HitResult trace = HitResultUtil.getLastHitResult();
        if (trace != null && trace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) trace;
            BlockPos pos = blockHitResult.getBlockPos();
            Block block = mc.level.getBlockState(pos).getBlock();
            mc.level.getBlockState(pos);
            if (block == Blocks.BEACON || mc.level.getBlockEntity(pos) instanceof Container) {
                interactionResult = InteractionResult.PASS;
            }
        }
        return interactionResult;
    }
}

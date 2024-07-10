package com.plusls.MasaGadget.mixin.mod_tweak.litematica.betterEasyPlaceMode;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.util.ModId;
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
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.util.collect.ValueContainer;

@Dependencies(require = @Dependency(ModId.litematica))
@Mixin(value = WorldUtils.class, remap = false)
public class MixinWorldUtils {
    @ModifyVariable(
            method = "handleEasyPlace",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lfi/dy/masa/litematica/util/WorldUtils;doEasyPlaceAction(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/world/InteractionResult;",
                    remap = true
            )
    )
    private static InteractionResult checkInventory(InteractionResult interactionResult) {
        Minecraft mc = Minecraft.getInstance();

        if (!Configs.betterEasyPlaceMode || mc.level == null || interactionResult != InteractionResult.FAIL) {
            return interactionResult;
        }

        ValueContainer<HitResult> trace = HitResultHandler.getInstance().getLastHitResult();

        if (trace.isEmpty()) {
            return interactionResult;
        }

        if (trace.get().getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) trace.get();
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

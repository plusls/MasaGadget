package com.plusls.MasaGadget.mixin.mod_tweak.litematica.fixCarpetAccurateProtocol;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.BlockPlacer;
import com.plusls.MasaGadget.util.ModId;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

// 用魔法打败魔法
// 提升优先级，确保是由 carpet extra 的协议处理的
// litematica 是 999
// tweakeroo 是 990
// carpet extra 是 1000
// 可以保证第一个执行自己的函数
@Dependencies(require = @Dependency(ModId.litematica))
@Mixin(value = BlockItem.class, priority = 980)
public abstract class MixinBlockItem {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract boolean canPlace(BlockPlaceContext context, BlockState state);

    @Inject(method = "getPlacementState", at = @At(value = "HEAD"), cancellable = true)
    private void preGetPlacementState(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!Configs.fixAccurateProtocol.getBooleanValue()) {
            return;
        }

        BlockState blockState = null;

        try {
            blockState = BlockPlacer.alternativeBlockPlacement(this.getBlock(), context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (blockState != null && this.canPlace(context, blockState)) {
            cir.setReturnValue(blockState);
        }
    }
}
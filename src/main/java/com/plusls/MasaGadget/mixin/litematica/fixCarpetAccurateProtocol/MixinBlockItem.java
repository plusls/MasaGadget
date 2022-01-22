package com.plusls.MasaGadget.mixin.litematica.fixCarpetAccurateProtocol;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.litematica.fixCarpetAccurateProtocol.BlockPlacer;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 用魔法打败魔法
// 提升优先级，确保是由 carpet extra 的协议处理的
// litematica 是 999
// tweakeroo 是 990
// carpet extra 是 1000
// 可以保证第一个执行自己的函数
@Mixin(value = BlockItem.class, priority = 980)
@Dependencies(dependencyList = @Dependency(modId = ModInfo.LITEMATICA_MOD_ID, version = "*"))
public abstract class MixinBlockItem {

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract boolean canPlace(ItemPlacementContext context, BlockState state);

    @Inject(method = "getPlacementState", at = @At(value = "HEAD"), cancellable = true)
    private void preGetPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!Configs.Litematica.FIX_ACCURATE_PROTOCOL.getBooleanValue()) {
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
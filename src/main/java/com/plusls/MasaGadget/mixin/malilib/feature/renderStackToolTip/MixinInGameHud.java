package com.plusls.MasaGadget.mixin.malilib.feature.renderStackToolTip;

import com.plusls.MasaGadget.malilib.util.InventoryOverlayRenderHandler;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 自己实现是为了调低优先级保证最后执行保证渲染在最上层
@Mixin(value = InGameHud.class, priority = 1001)
public abstract class MixinInGameHud extends DrawableHelper {

    @Inject(method = "render", at = @At("RETURN"))
    private void onGameOverlayPost(MatrixStack matrixStack, float partialTicks, CallbackInfo ci) {
        InventoryOverlayRenderHandler.instance.render(matrixStack);
    }
}

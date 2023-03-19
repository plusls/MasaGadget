package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportSelect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

// 自己实现是为了调低优先级保证最后执行保证渲染在最上层
@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = Gui.class, priority = 1001)
public abstract class MixinInGameHud {

    @Inject(method = "render", at = @At("RETURN"))
    //#if MC > 11502
    private void onGameOverlayPost(PoseStack matrixStack, float partialTicks, CallbackInfo ci) {
        //#else
        //$$ private void onGameOverlayPost(float partialTicks, CallbackInfo ci) {
        //#endif
        if (Configs.inventoryPreviewSupportSelect) {
            //#if MC > 11502
            InventoryOverlayRenderHandler.instance.render(matrixStack);
            //#else
            //$$ InventoryOverlayRenderHandler.instance.render(new PoseStack());
            //#endif
        }
    }
}

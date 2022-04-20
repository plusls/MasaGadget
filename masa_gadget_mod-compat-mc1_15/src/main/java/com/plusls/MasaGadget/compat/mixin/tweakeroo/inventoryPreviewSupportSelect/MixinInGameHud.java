package com.plusls.MasaGadget.compat.mixin.tweakeroo.inventoryPreviewSupportSelect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = Gui.class, priority = 1001)
public abstract class MixinInGameHud {

    @Inject(method = "render", at = @At("RETURN"))
    private void onGameOverlayPost(float f, CallbackInfo ci) {
        if (Configs.inventoryPreviewSupportSelect) {
            InventoryOverlayRenderHandler.instance.render(new PoseStack());
        }
    }
}

package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import com.plusls.MasaGadget.util.ModId;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.render.context.RenderContext;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc26.2: subproject 1.16.5 (main project)</li>
 * <li>mc26.3+        : subproject 26.3        &lt;--------</li>
 */
// CHECKSTYLE.ON: JavadocStyle
@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = Hud.class, priority = 1100)
public abstract class MixinInGameHud {
    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void onGameOverlayPost(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (Configs.inventoryPreviewSupportSelect.getBooleanValue()) {
            InventoryOverlayRenderHandler.getInstance().render(RenderContext.gui(guiGraphics));
        }
    }
}

package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import com.plusls.MasaGadget.util.ModId;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.render.context.RenderContext;

import net.minecraft.client.gui.Gui;

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 26.1
//$$ import net.minecraft.client.gui.GuiGraphicsExtractor;
//#elseif MC >= 1.20
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC >= 1.16
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

//#if MC > 12006
//$$ import net.minecraft.client.DeltaTracker;
//#endif
// CHECKSTYLE.ON: ImportOrder

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = Gui.class, priority = 1100)
public abstract class MixinInGameHud {
    @Inject(
            //#if MC >= 26.1
            //$$ method = "extractRenderState",
            //#else
            method = "render",
            //#endif
            at = @At("RETURN")
    )
    private void onGameOverlayPost(
            //#if MC >= 26.1
            //$$ GuiGraphicsExtractor poseStackOrGuiGraphics,
            //#elseif MC >= 1.20
            //$$ GuiGraphics poseStackOrGuiGraphics,
            //#elseif MC >= 1.16
            PoseStack poseStackOrGuiGraphics,
            //#endif
            //#if MC > 12006
            //$$ DeltaTracker deltaTracker,
            //#else
            float partialTicks,
            //#endif
            CallbackInfo ci
    ) {
        if (Configs.inventoryPreviewSupportSelect.getBooleanValue()) {
            InventoryOverlayRenderHandler.getInstance().render(RenderContext.gui(
                    //#if MC > 11502
                    poseStackOrGuiGraphics
                    //#endif
            ));
        }
    }
}

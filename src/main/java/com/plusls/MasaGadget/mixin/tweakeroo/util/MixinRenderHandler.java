package com.plusls.MasaGadget.mixin.tweakeroo.util;

import com.plusls.MasaGadget.malilib.util.InventoryOverlayRenderHandler;
import com.plusls.MasaGadget.tweakeroo.pcaSyncProtocol.PcaSyncProtocol;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = RenderHandler.class, remap = false)
public abstract class MixinRenderHandler implements IRenderer {
    @Redirect(method = "onRenderGameOverlayPost",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/hotkeys/IKeybind;isKeybindHeld()Z",
                    ordinal = 2))
    private boolean checkInventoryPreviewPress(IKeybind iKeybind) {
        boolean ret = iKeybind.isKeybindHeld();
        if (!ret) {
            if (PcaSyncProtocol.enable) {
                // 未按下按键时若是 lastBlockPos 不为空， 则告诉服务端不需要更新 block entity
                PcaSyncProtocol.cancelSyncBlockEntity();
                PcaSyncProtocol.cancelSyncEntity();
            }
            // 重置预览选择槽
            InventoryOverlayRenderHandler.instance.resetSelectedIdx();
        }
        return ret;
    }
}

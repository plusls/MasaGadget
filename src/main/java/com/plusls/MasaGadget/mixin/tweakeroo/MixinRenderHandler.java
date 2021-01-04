package com.plusls.MasaGadget.mixin.tweakeroo;

import com.plusls.MasaGadget.network.PcaSyncProtocol;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = RenderHandler.class, remap = false)
public abstract class MixinRenderHandler implements IRenderer {
    // 未按下按键时若是 lastBlockPos 不为空， 则告诉服务端不需要更新 block entity
    @Redirect(method = "onRenderGameOverlayPost",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/hotkeys/IKeybind;isKeybindHeld()Z",
                    ordinal = 2))
    private boolean cancelPcaSync(IKeybind iKeybind) {
        boolean ret = iKeybind.isKeybindHeld();
        if (!ret) {
            PcaSyncProtocol.cancelSyncBlockEntity();
            PcaSyncProtocol.cancelSyncEntity();
        }
        return ret;
    }
}

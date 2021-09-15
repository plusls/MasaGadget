package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 自己实现是为了调低优先级保证最后执行保证渲染在最上层
@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = InGameHud.class, priority = 1001)
public abstract class MixinInGameHud extends DrawableHelper {

    @Inject(method = "render", at = @At("RETURN"))
    private void onGameOverlayPost(float tickDelta, CallbackInfo ci) {
        if (Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_SELECT.getBooleanValue()) {
            InventoryOverlayRenderHandler.instance.render();
        }
    }
}

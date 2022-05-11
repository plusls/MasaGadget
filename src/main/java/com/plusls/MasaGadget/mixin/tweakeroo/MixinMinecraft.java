package com.plusls.MasaGadget.mixin.tweakeroo;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.tweakeroo.InventoryPreviewUtil;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void postTick(CallbackInfo ci) {
        InventoryPreviewUtil.setLastInventoryPreviewStatus(Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld());
    }
}

package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportComparator;

import com.plusls.MasaGadget.ModInfo;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    // dummy impl
    //see 1.14.4
}

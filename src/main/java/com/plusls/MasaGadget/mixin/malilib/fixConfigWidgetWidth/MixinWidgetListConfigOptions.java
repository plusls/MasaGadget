package com.plusls.MasaGadget.mixin.malilib.fixConfigWidgetWidth;

import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(value = "minecraft", versionPredicate = "<=1.17.1"))
@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions {

    @Dynamic
    @Redirect(method = "getMaxNameLengthWrapped",
            at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/config/IConfigBase;getName()Ljava/lang/String;", ordinal = 0))
    private String fixWidth(IConfigBase instance) {
        if (Configs.fixConfigWidgetWidth) {
            return instance.getConfigGuiDisplayName();
        }
        return instance.getName();
    }

}
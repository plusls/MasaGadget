package com.plusls.MasaGadget.mixin.malilib.backportI18nSupport;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBase;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.language.I18n;

@Dependencies(and = @Dependency(value = "minecraft", versionPredicate = "<=1.17.1"))
@Mixin(value = ConfigBase.class, remap = false)
public abstract class MixinConfigBase implements IConfigBase {

    @Nullable
    private static final Class<?> tweakerMoreIConfigBaseClass;

    static {
        @Nullable Class<?> tweakerMoreIConfigBaseClass1;
        try {
            tweakerMoreIConfigBaseClass1 = Class.forName("me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase");
        } catch (ClassNotFoundException e) {
            tweakerMoreIConfigBaseClass1 = null;
        }
        tweakerMoreIConfigBaseClass = tweakerMoreIConfigBaseClass1;
    }

    @Inject(method = "getComment", at = @At(value = "RETURN"), cancellable = true)
    private void useI18nComment(CallbackInfoReturnable<String> cir) {
        if (Configs.backportI18nSupport) {
            cir.setReturnValue(MiscUtil.getTranslatedOrFallback("config.comment." + this.getName().toLowerCase(),
                    cir.getReturnValue()));
        }
    }

    @Override
    public String getConfigGuiDisplayName() {

        if (tweakerMoreIConfigBaseClass != null && tweakerMoreIConfigBaseClass.isInstance(this)) {
            return I18n.get("tweakermore.config." + this.getName());
        } else if (Configs.backportI18nSupport) {
            return MiscUtil.getTranslatedOrFallback("config.name." + this.getName().toLowerCase(), this.getName());
        } else {
            return this.getName();
        }
    }
}
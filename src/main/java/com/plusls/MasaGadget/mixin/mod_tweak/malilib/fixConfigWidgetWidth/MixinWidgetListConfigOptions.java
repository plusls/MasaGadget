package com.plusls.MasaGadget.mixin.mod_tweak.malilib.fixConfigWidgetWidth;

import com.plusls.MasaGadget.game.Configs;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.impl.malilib.config.gui.MagicConfigGui;
import top.hendrixshen.magiclib.util.ReflectionUtil;
import top.hendrixshen.magiclib.util.collect.ValueContainer;

import java.util.List;
import java.util.Objects;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = "<=1.17.1"))
@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {
    @Unique
    private static final ValueContainer<Class<?>> masa_gadget$tweakerMoreIConfigBaseClass = ReflectionUtil
            .getClass("me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui");

    @Shadow
    @Final
    protected GuiConfigsBase parent;

    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }

    @Inject(
            method = "getMaxNameLengthWrapped",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
            ),
            cancellable = true
    )
    private void fixWidth(List<GuiConfigsBase.ConfigOptionWrapper> wrappers, CallbackInfoReturnable<Integer> cir) {
        // MagicConfigGui was fixed by magiclib.
        if (this.parent instanceof MagicConfigGui) {
            return;
        }

        // TweakerMore manager it ui by itself.
        if (MixinWidgetListConfigOptions.masa_gadget$tweakerMoreIConfigBaseClass
                .filter(clazz -> clazz.isInstance(this.parent)).isPresent()) {
            return;
        }

        if (!Configs.fixConfigWidgetWidth.getBooleanValue()) {
            return;
        }

        int maxWidth = 0;

        for (GuiConfigsBase.ConfigOptionWrapper wrapper : wrappers) {
            if (wrapper.getType() == GuiConfigsBase.ConfigOptionWrapper.Type.CONFIG) {
                maxWidth = Math.max(maxWidth, this.getStringWidth(
                        Objects.requireNonNull(wrapper.getConfig()).getConfigGuiDisplayName()));
            }
        }

        cir.setReturnValue(maxWidth);
    }
}

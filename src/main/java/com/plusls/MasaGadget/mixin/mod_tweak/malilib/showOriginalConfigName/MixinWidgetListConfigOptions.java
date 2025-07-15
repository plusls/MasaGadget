package com.plusls.MasaGadget.mixin.mod_tweak.malilib.showOriginalConfigName;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.sugar.Local;

import java.util.Objects;

@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {
    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }

    @ModifyVariable(
            method = "getMaxNameLengthWrapped",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;max(II)I",
                    shift = At.Shift.AFTER
            )
    )
    private int recalcMaxNameLength(int width, @Local GuiConfigsBase.ConfigOptionWrapper wrapper) {
        if (Configs.showOriginalConfigName.getBooleanValue()) {
            String displayName = MiscUtil.getStringWithoutFormat(Objects.requireNonNull(
                    wrapper.getConfig()).getConfigGuiDisplayName());
            String name = wrapper.getConfig().getName();

            if (!displayName.equals(name)) {
                width = (int) Math.max(width, this.getStringWidth(name) * Configs.showOriginalConfigNameScale.getDoubleValue());
            }
        }

        return width;
    }
}

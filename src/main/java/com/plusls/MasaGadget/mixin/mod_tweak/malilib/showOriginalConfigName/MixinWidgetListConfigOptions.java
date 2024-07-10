package com.plusls.MasaGadget.mixin.mod_tweak.malilib.showOriginalConfigName;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;

// TODO: Rewrite
@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {
    @Unique
    private GuiConfigsBase.ConfigOptionWrapper masa_gadget$maxLengthConfig;

    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }

    @ModifyVariable(
            method = "getMaxNameLengthWrapped",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;max(II)I"
            )
    )
    private GuiConfigsBase.ConfigOptionWrapper getWrapper(GuiConfigsBase.ConfigOptionWrapper value) {
        this.masa_gadget$maxLengthConfig = value;
        return value;
    }

    @ModifyVariable(
            method = "getMaxNameLengthWrapped",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Ljava/lang/Math;max(II)I"
            )
    )
    private int getWrapper(int width) {
        if (Configs.showOriginalConfigName.getBooleanValue()) {
            String displayName = MiscUtil.getStringWithoutFormat(Objects.requireNonNull(
                    this.masa_gadget$maxLengthConfig.getConfig()).getConfigGuiDisplayName());
            String name = this.masa_gadget$maxLengthConfig.getConfig().getName();

            if (!displayName.equals(name)) {
                width = Math.max(width, (int) Math.ceil(this.getStringWidth(name) * Configs.showOriginalConfigNameScale.getDoubleValue()));
            }
        }

        return width;
    }
}

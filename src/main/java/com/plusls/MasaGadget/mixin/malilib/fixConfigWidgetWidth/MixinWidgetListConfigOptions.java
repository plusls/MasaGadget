package com.plusls.MasaGadget.mixin.malilib.fixConfigWidgetWidth;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.malilib.util.WidgetUtil;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {

    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }


    @Inject(method = "reCreateListEntryWidgets", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/gui/widgets/WidgetListConfigOptionsBase;reCreateListEntryWidgets()V"))
    private void fixMaxLabelWidth(CallbackInfo ci) {
        if (Configs.Malilib.FIX_CONFIG_WIDGET_WIDTH.getBooleanValue()) {
            this.maxLabelWidth = this.getMaxGuiDisplayNameLengthWrapped(this.listContents);
        }
    }

    private int getMaxGuiDisplayNameLengthWrapped(List<GuiConfigsBase.ConfigOptionWrapper> wrappers) {
        int width = 0;

        for (GuiConfigsBase.ConfigOptionWrapper wrapper : wrappers) {
            if (wrapper.getType() == GuiConfigsBase.ConfigOptionWrapper.Type.CONFIG) {
                IConfigBase configBase = wrapper.getConfig();
                if (configBase == null) {
                    continue;
                }
                width = Math.max(width, this.getStringWidth(WidgetUtil.getTranslatedGuiDisplayName(configBase)));
            }
        }
        return width;
    }
}


package com.plusls.MasaGadget.mixin.malilib.fix.configWidgetWidth;

import com.plusls.MasaGadget.malilib.util.WidgetUtil;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(WidgetListConfigOptions.class)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {

    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }

    /**
     * @author plusls
     * @reason fix render width error
     */
    @Overwrite(remap = false)
    protected void reCreateListEntryWidgets() {
        this.maxLabelWidth = this.getMaxGuiDisplayNameLengthWrapped(this.listContents);
        super.reCreateListEntryWidgets();
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


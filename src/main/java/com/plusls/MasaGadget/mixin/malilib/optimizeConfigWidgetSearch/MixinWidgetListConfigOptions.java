package com.plusls.MasaGadget.mixin.malilib.optimizeConfigWidgetSearch;

import com.google.common.collect.ImmutableList;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.malilib.util.WidgetUtil;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {
    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }

    @Inject(method = "getEntryStringsForFilter*", at = @At(value = "HEAD"), cancellable = true)
    private void preGetEntryStringsForFilter(GuiConfigsBase.ConfigOptionWrapper entry, CallbackInfoReturnable<List<String>> cir) {
        if (!Configs.Malilib.OPTIMIZE_CONFIG_WIDGET_SEARCH.getBooleanValue()) {
            return;
        }
        IConfigBase config = entry.getConfig();
        if (config != null) {
            if (config instanceof IConfigResettable && ((IConfigResettable) config).isModified()) {
                cir.setReturnValue(ImmutableList.of(WidgetUtil.getTranslatedGuiDisplayName(config).toLowerCase(), config.getName().toLowerCase(), "modified"));
            } else {
                cir.setReturnValue(ImmutableList.of(WidgetUtil.getTranslatedGuiDisplayName(config).toLowerCase(), config.getName().toLowerCase()));
            }
        }
    }


    // fix upper case when search Disable Hotkeys
    @Override
    protected boolean matchesFilter(List<String> entryStrings, String filterText) {
        if (!Configs.Malilib.OPTIMIZE_CONFIG_WIDGET_SEARCH.getBooleanValue()) {
            return super.matchesFilter(entryStrings, filterText);
        }
        filterText = filterText.toLowerCase();
        if (filterText.isEmpty()) {
            return true;
        }

        for (String str : entryStrings) {
            if (this.matchesFilter(str, filterText)) {
                return true;
            }
        }
        return false;
    }
}


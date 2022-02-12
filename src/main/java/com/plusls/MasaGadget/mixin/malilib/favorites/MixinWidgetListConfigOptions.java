package com.plusls.MasaGadget.mixin.malilib.favorites;

import com.google.common.collect.ImmutableList;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {

    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }

    @Inject(method = "getEntryStringsForFilter*", at = @At(value = "HEAD"), cancellable = true)
    private void filterFavorites(GuiConfigsBase.ConfigOptionWrapper entry, CallbackInfoReturnable<List<String>> cir) {
        if (Configs.Malilib.FAVORITES_SUPPORT.getBooleanValue() && Configs.Malilib.favoritesFilter) {
            IConfigBase config = entry.getConfig();
            if (config == null || !Configs.Malilib.FAVORITES.getOrDefault(config.getName(), false)) {
                cir.setReturnValue(ImmutableList.of(""));
            }
        }
    }

    @Override
    protected void addNonFilteredContents(Collection<GuiConfigsBase.ConfigOptionWrapper> placements) {
        if (Configs.Malilib.FAVORITES_SUPPORT.getBooleanValue() && Configs.Malilib.favoritesFilter) {
            for (GuiConfigsBase.ConfigOptionWrapper configWrapper : placements) {
                IConfigBase config = configWrapper.getConfig();
                if (config != null && Configs.Malilib.FAVORITES.getOrDefault(config.getName(), false)) {
                    this.listContents.add(configWrapper);
                }
            }
        } else {
            this.listContents.addAll(placements);
        }
    }
}

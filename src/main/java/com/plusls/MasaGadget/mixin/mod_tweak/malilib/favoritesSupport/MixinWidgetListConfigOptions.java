package com.plusls.MasaGadget.mixin.mod_tweak.malilib.favoritesSupport;

import com.google.common.collect.Sets;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport.MalilibFavoritesData;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {
    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }

    @Inject(method = "getEntryStringsForFilter*", at = @At("HEAD"), cancellable = true)
    private void filterFavorites(GuiConfigsBase.ConfigOptionWrapper entry, CallbackInfoReturnable<List<String>> cir) {
        if (Configs.favoritesSupport.getBooleanValue() && MalilibFavoritesData.getInstance().isFilterSwitch()) {
            IConfigBase config = entry.getConfig();
            Screen screen = Minecraft.getInstance().screen;

            if (!(screen instanceof GuiConfigsBase)) {
                return;
            }

            String modId = ((GuiConfigsBase) screen).getModId();

            if (config == null || !MalilibFavoritesData.getInstance().getFavorites()
                    .computeIfAbsent(modId, k -> Sets.newHashSet()).contains(config.getName())) {
                cir.setReturnValue(Collections.emptyList());
            }
        }
    }

    // TODO: Compat
    @Override
    protected void addNonFilteredContents(Collection<GuiConfigsBase.ConfigOptionWrapper> placements) {
        if (Configs.favoritesSupport.getBooleanValue() && MalilibFavoritesData.getInstance().isFilterSwitch()) {
            Screen screen = Minecraft.getInstance().screen;

            if (!(screen instanceof GuiConfigsBase)) {
                return;
            }

            String modId = ((GuiConfigsBase) screen).getModId();

            for (GuiConfigsBase.ConfigOptionWrapper configWrapper : placements) {
                IConfigBase config = configWrapper.getConfig();

                if (config != null && MalilibFavoritesData.getInstance().getFavorites()
                        .computeIfAbsent(modId, k -> Sets.newHashSet()).contains(config.getName())) {
                    this.listContents.add(configWrapper);
                }
            }
        } else {
            this.listContents.addAll(placements);
        }
    }
}

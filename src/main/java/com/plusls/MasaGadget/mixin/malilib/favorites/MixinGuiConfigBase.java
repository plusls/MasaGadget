package com.plusls.MasaGadget.mixin.malilib.favorites;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.gui.MasaGadgetIcons;
import com.plusls.MasaGadget.gui.WidgetIconToggleButton;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiConfigsBase.class, remap = false)
public abstract class MixinGuiConfigBase extends GuiListBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> implements IKeybindConfigGui {
    private final WidgetIconToggleButton favoritesButton = new WidgetIconToggleButton(GuiUtils.getScaledWindowWidth() - 175, 13,
            MasaGadgetIcons.FAVORITE, Configs.Malilib.favorites,
            status -> {
                Configs.Malilib.favorites = status;
                WidgetListConfigOptions widgetListConfigOptions = this.getListWidget();
                if (widgetListConfigOptions != null) {
                    widgetListConfigOptions.refreshEntries();
                }
            },
            status -> status ? I18n.translate("masa_gadget_mod.message.showAllOptions") : I18n.translate("masa_gadget_mod.message.showFavorite"),
            widgetIconToggleButton -> Configs.Malilib.FAVORITES_SUPPORT.getBooleanValue());

    protected MixinGuiConfigBase(int listX, int listY) {
        super(listX, listY);
    }

    @Inject(method = "initGui", at = @At(value = "RETURN"))
    public void postInitGui(CallbackInfo ci) {
        this.addWidget(favoritesButton);
    }

    @Dynamic
    @Inject(method = "resize", at = @At(value = "HEAD"))
    public void favoritesResize(MinecraftClient mc, int width, int height, CallbackInfo callbackInfo) {
        favoritesButton.setX(GuiUtils.getScaledWindowWidth() - 175);
    }
}

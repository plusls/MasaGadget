package com.plusls.MasaGadget.mixin.malilib.favorites;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.gui.MasaGadgetIcons;
import com.plusls.MasaGadget.gui.WidgetIconToggleButton;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

@Mixin(value = WidgetConfigOption.class, remap = false)
public abstract class MixinWidgetConfigOption extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper> {
    public MixinWidgetConfigOption(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex) {
        super(x, y, width, height, parent, entry, listIndex);
    }

    @Inject(method = "addConfigOption", at = @At(value = "HEAD"))
    private void addFavoritesButton(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config, CallbackInfo ci) {
        if (!Configs.favoritesSupport) {
            return;
        }

        Screen screen = Minecraft.getInstance().screen;
        if (!(screen instanceof GuiConfigsBase)) {
            return;
        }
        String modId = ((GuiConfigsBase) screen).getModId();

        this.addWidget(new WidgetIconToggleButton(x + labelWidth + configWidth + 25 +
                this.getStringWidth(I18n.get("malilib.gui.button.reset.caps")), y + 3,
                MasaGadgetIcons.FAVORITE, Configs.FAVORITES.computeIfAbsent(modId, k -> new HashSet<>()).contains(config.getName()),
                status -> {
                    HashSet<String> modFavorites = Configs.FAVORITES.computeIfAbsent(modId, k -> new HashSet<>());
                    if (status) {
                        modFavorites.add(config.getName());
                    } else {
                        modFavorites.remove(config.getName());
                    }
                    ModInfo.configHandler.saveToFile();
                    ModInfo.configHandler.loadFromFile();
                },
                status -> status ? ModInfo.translate("message.cancelFavorite") : ModInfo.translate("message.setFavorite"),
                widgetIconToggleButton -> Configs.favoritesSupport));


    }
}

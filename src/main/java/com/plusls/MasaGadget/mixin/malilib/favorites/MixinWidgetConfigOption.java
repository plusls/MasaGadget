package com.plusls.MasaGadget.mixin.malilib.favorites;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.gui.MasaGadgetIcons;
import com.plusls.MasaGadget.gui.WidgetIconToggleButton;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
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

    @Inject(method = "addConfigOption", at = @At(value = "RETURN"))
    private void addFavoritesButton(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config, CallbackInfo ci) {
        if (!Configs.Malilib.FAVORITES_SUPPORT.getBooleanValue()) {
            return;
        }
        ConfigType type = config.getType();
        if (type == ConfigType.COLOR) {
            configWidth += 22;
        } else if (type == ConfigType.INTEGER || type == ConfigType.DOUBLE) {
            configWidth += 18;
        }

        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (!(screen instanceof GuiConfigsBase)) {
            return;
        }
        String modId = ((GuiConfigsBase) screen).getModId();

        this.addWidget(new WidgetIconToggleButton(x + configWidth + 15 +
                this.getStringWidth(I18n.translate("malilib.gui.button.reset.caps")), y + 2,
                MasaGadgetIcons.FAVORITE, Configs.Malilib.FAVORITES.computeIfAbsent(modId, k -> new HashSet<>()).contains(config.getName()),
                status -> {
                    HashSet<String> modFavorites = Configs.Malilib.FAVORITES.computeIfAbsent(modId, k -> new HashSet<>());
                    if (status) {
                        modFavorites.add(config.getName());
                    } else {
                        modFavorites.remove(config.getName());
                    }
                    Configs.saveToFile();
                    Configs.loadFromFile();
                },
                status -> status ? I18n.translate("masa_gadget_mod.message.cancelFavorite") : I18n.translate("masa_gadget_mod.message.setFavorite"),
                widgetIconToggleButton -> Configs.Malilib.FAVORITES_SUPPORT.getBooleanValue()));


    }
}

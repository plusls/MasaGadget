package com.plusls.MasaGadget.mixin.mod_tweak.malilib.favoritesSupport;

import com.google.common.collect.Sets;
import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport.MalilibFavoritesButton;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport.MalilibFavoritesData;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.i18n.I18n;

import java.util.Set;

@Mixin(value = WidgetConfigOption.class, remap = false)
public abstract class MixinWidgetConfigOption extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper> {
    public MixinWidgetConfigOption(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex) {
        super(x, y, width, height, parent, entry, listIndex);
    }

    @Inject(
            method = "addConfigOption",
            at = @At(
                    value = "HEAD"
            )
    )
    private void addFavoritesButton(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config, CallbackInfo ci) {
        if (!Configs.favoritesSupport.getBooleanValue()) {
            return;
        }

        Screen screen = Minecraft.getInstance().screen;

        if (!(screen instanceof GuiConfigsBase)) {
            return;
        }

        String modId = ((GuiConfigsBase) screen).getModId();

        this.addWidget(MalilibFavoritesButton.create(x + labelWidth + configWidth + 25 +
                        this.getStringWidth(I18n.tr("malilib.gui.button.reset.caps")), y + 3,
                MalilibFavoritesData.getInstance().getFavorites()
                        .computeIfAbsent(modId, k -> Sets.newHashSet()).contains(config.getName()),
                status -> {
                    Set<String> modFavorites = MalilibFavoritesData.getInstance().getFavorites()
                            .computeIfAbsent(modId, k -> Sets.newHashSet());

                    if (status) {
                        modFavorites.add(config.getName());
                    } else {
                        modFavorites.remove(config.getName());
                    }

                    SharedConstants.getConfigHandler().save();
                },
                status -> status ? I18n.tr("masa_gadget_mod.message.cancelFavorite") :
                        I18n.tr("masa_gadget_mod.message.setFavorite")));
    }
}

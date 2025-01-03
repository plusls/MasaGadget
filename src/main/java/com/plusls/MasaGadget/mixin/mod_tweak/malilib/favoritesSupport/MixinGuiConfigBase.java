package com.plusls.MasaGadget.mixin.mod_tweak.malilib.favoritesSupport;

import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.api.fake.mod_tweak.malilib.favoritesSupport.GuiBaseInjector;
import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport.MalilibFavoritesButton;
import com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport.MalilibFavoritesData;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.i18n.I18n;
import top.hendrixshen.magiclib.util.collect.ValueContainer;

@Mixin(value = GuiConfigsBase.class, remap = false, priority = 1100)
public abstract class MixinGuiConfigBase extends GuiListBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> implements GuiBaseInjector {
    protected MixinGuiConfigBase(int listX, int listY) {
        super(listX, listY);
    }

    @Override
    public void masa_gadget_mod$addFavoritesWidget() {
        if (!Configs.favoritesSupport.getBooleanValue()) {
            return;
        }

        int xOffset = Configs.fastSwitchMasaConfigGui.getBooleanValue() ? 132 : 28;
        MalilibFavoritesButton favoritesButton = MalilibFavoritesButton.create(
                this.width - xOffset, 10, MalilibFavoritesData.getInstance().isFilterSwitch(),
                status -> {
                    MalilibFavoritesData.getInstance().setFilterSwitch(status);
                    ValueContainer.ofNullable(this.getListWidget()).ifPresent(w -> {
                        w.getScrollbar().setValue(0);
                        w.refreshEntries();
                    });
                    SharedConstants.getConfigHandler().save();
                },
                status -> status ? I18n.tr("masa_gadget_mod.message.showAllOptions") :
                        I18n.tr("masa_gadget_mod.message.showFavorite"));
        this.addWidget(favoritesButton);
    }
}

package com.plusls.MasaGadget.game;

import com.plusls.MasaGadget.SharedConstants;
import fi.dy.masa.malilib.gui.GuiBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.api.i18n.I18n;
import top.hendrixshen.magiclib.impl.malilib.config.gui.MagicConfigGui;
import top.hendrixshen.magiclib.util.collect.ValueContainer;

public class ConfigGui extends MagicConfigGui {
    @Nullable
    private static ConfigGui currentInstance = null;

    public ConfigGui() {
        super(
                SharedConstants.getModIdentifier(),
                SharedConstants.getConfigManager(),
                I18n.tr("masa_gadget_mod.gui.title.configs", SharedConstants.getModVersion())
        );
    }

    @Override
    public void init() {
        super.init();
        ConfigGui.currentInstance = this;
    }

    @Override
    public void removed() {
        super.removed();
        ConfigGui.currentInstance = null;
    }

    @Override
    public boolean isDebug() {
        return Configs.debug.getBooleanValue();
    }

    public static void openGui() {
        GuiBase.openGui(new ConfigGui());
    }

    @Override
    public boolean hideUnAvailableConfigs() {
        return top.hendrixshen.magiclib.game.malilib.Configs.hideUnavailableConfigs.getBooleanValue();
    }

    public static @NotNull ValueContainer<ConfigGui> getCurrentInstance() {
        return ValueContainer.ofNullable(ConfigGui.currentInstance);
    }
}

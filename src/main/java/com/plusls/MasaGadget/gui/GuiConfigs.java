package com.plusls.MasaGadget.gui;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.resource.language.I18n;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GuiConfigs extends GuiConfigsBase {
    public static ConfigGuiTab tab = ConfigGuiTab.GENERIC;

    public GuiConfigs() {
        super(10, 50, ModInfo.MOD_ID, null, String.format("%s.gui.title.configs", ModInfo.MOD_ID));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;
        int rows = 1;
        for (ConfigGuiTab tab : ConfigGuiTab.values()) {
            if (!MasaGadgetMixinPlugin.isLitematicaLoaded && tab == ConfigGuiTab.LITEMATICA) {
                continue;
            } else if (!MasaGadgetMixinPlugin.isMinihudLoaded && tab == ConfigGuiTab.MINIHUD) {
                continue;
            } else if (!MasaGadgetMixinPlugin.isTweakerooLoaded && tab == ConfigGuiTab.TWEAKEROO) {
                continue;
            }
            int width = this.getStringWidth(tab.getDisplayName()) + 10;
            if (x >= this.width - width - 10) {
                x = 10;
                y += 22;
                rows++;
            }

            x += this.createButton(x, y, width, tab);
        }

        if (rows > 1) {
            int scrollbarPosition = Objects.requireNonNull(this.getListWidget()).getScrollbar().getValue();
            this.setListPosition(this.getListX(), 50 + (rows - 1) * 22);
            this.reCreateListWidget();
            this.getListWidget().getScrollbar().setValue(scrollbarPosition);
            this.getListWidget().refreshEntries();
        }
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(GuiConfigs.tab != tab);
        this.addButton(button, new ButtonListenerConfigTabs(tab, this));
        return button.getWidth() + 2;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = GuiConfigs.tab;
        if (tab == ConfigGuiTab.GENERIC) {
            configs = Configs.Generic.OPTIONS;
        } else if (tab == ConfigGuiTab.LITEMATICA) {
            configs = Configs.Litematica.GUI_OPTIONS;
        } else if (tab == ConfigGuiTab.MALILIB) {
            configs = Configs.Malilib.OPTIONS;
        } else if (tab == ConfigGuiTab.MINIHUD) {
            configs = Configs.Minihud.GUI_OPTIONS;
        } else if (tab == ConfigGuiTab.TWEAKEROO) {
            configs = Configs.Tweakeroo.OPTIONS;
        } else {
            return Collections.emptyList();
        }
        return ConfigOptionWrapper.createFor(configs);
    }

    private static class ButtonListenerConfigTabs implements IButtonActionListener {
        private final GuiConfigs parent;
        private final ConfigGuiTab tab;

        public ButtonListenerConfigTabs(ConfigGuiTab tab, GuiConfigs parent) {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            GuiConfigs.tab = this.tab;
            this.parent.reCreateListWidget(); // apply the new config width
            Objects.requireNonNull(this.parent.getListWidget()).resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum ConfigGuiTab {
        GENERIC("generic"),
        LITEMATICA("litematica"),
        MALILIB("malilib"),
        MINIHUD("minihud"),
        TWEAKEROO("tweakeroo");

        private final String name;

        ConfigGuiTab(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return I18n.translate(String.format("%s.gui.button.configGui.%s", ModInfo.MOD_ID, name));
        }
    }
}

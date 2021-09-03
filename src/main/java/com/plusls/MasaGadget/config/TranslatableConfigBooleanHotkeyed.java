package com.plusls.MasaGadget.config;

import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class TranslatableConfigBooleanHotkeyed extends ConfigBooleanHotkeyed {
    private final String guiDisplayName;

    public TranslatableConfigBooleanHotkeyed(String prefix, String name, boolean defaultValue, String defaultHotkey) {
        super(name, defaultValue, defaultHotkey, String.format("%s.%s.comment", prefix, name),
                String.format("%s.%s.pretty_name", prefix, name));
        this.guiDisplayName = String.format("%s.%s.name", prefix, name);
    }

    public TranslatableConfigBooleanHotkeyed(String prefix, String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings) {
        super(name, defaultValue, defaultHotkey, settings, String.format("%s.%s.comment", prefix, name),
                String.format("%s.%s.pretty_name", prefix, name));
        this.guiDisplayName = String.format("%s.%s.name", prefix, name);
    }

    @Override
    public String getPrettyName() {
        String ret = super.getPrettyName();
        if (ret.contains("pretty_name")) {
            ret = StringUtils.splitCamelCase(this.getConfigGuiDisplayName());
        }
        return ret;
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(this.guiDisplayName);
    }
}
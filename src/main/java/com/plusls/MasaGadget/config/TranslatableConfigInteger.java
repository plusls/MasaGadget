package com.plusls.MasaGadget.config;

import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.util.StringUtils;

public class TranslatableConfigInteger extends ConfigInteger {
    private final String guiDisplayName;

    public TranslatableConfigInteger(String prefix, String name, int defaultValue) {
        this(prefix, name, defaultValue, -2147483648, 2147483647);
    }

    public TranslatableConfigInteger(String prefix, String name, int defaultValue, int minValue, int maxValue) {
        this(prefix, name, defaultValue, minValue, maxValue, false);
    }

    public TranslatableConfigInteger(String prefix, String name, int defaultValue, int minValue, int maxValue, boolean useSlider) {
        super(name, defaultValue, minValue, maxValue, false, String.format("%s.%s.comment", prefix, name));
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
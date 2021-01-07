package com.plusls.MasaGadget.malilib.util;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetUtil {
    static public String getTranslatedGuiDisplayName(IConfigBase config) {
        return StringUtils.translate(config.getConfigGuiDisplayName());
    }
}

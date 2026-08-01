package com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc26.1: subproject 1.16.5 (main project)</li>
 * <li>mc26.1.2+      : subproject 26.1.2        &lt;--------</li>
 */
// CHECKSTYLE.ON: JavadocStyle
public final class MalilibJsonCompat {
    private MalilibJsonCompat() {
    }

    public static JsonObject getNestedObject(JsonObject jsonObject, String memberName) {
        return JsonUtils.getNestedObject(jsonObject, memberName, false);
    }
}

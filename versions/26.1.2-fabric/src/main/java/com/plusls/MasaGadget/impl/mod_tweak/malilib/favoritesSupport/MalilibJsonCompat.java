package com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

public final class MalilibJsonCompat {
    private MalilibJsonCompat() {
    }

    public static JsonObject getNestedObject(JsonObject jsonObject, String memberName) {
        return JsonUtils.getNestedObject(jsonObject, memberName, false);
    }
}

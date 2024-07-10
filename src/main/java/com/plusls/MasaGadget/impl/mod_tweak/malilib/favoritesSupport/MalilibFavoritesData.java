package com.plusls.MasaGadget.impl.mod_tweak.malilib.favoritesSupport;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.util.serializable.JsonSaveAble;

import java.util.*;

@Getter
@Setter
public class MalilibFavoritesData implements JsonSaveAble {
    @Getter
    private static final MalilibFavoritesData instance = new MalilibFavoritesData();

    private final Map<String, Set<String>> favorites = Maps.newHashMap();
    private boolean filterSwitch;

    @Override
    public void dumpToJson(@NotNull JsonObject jsonObject) {
        jsonObject.addProperty("filter_switch", this.filterSwitch);
        JsonObject dataObj = new JsonObject();

        for (Map.Entry<String, Set<String>> entry : this.favorites.entrySet()) {
            JsonArray modFavoriteObj = new JsonArray();

            if (!entry.getValue().isEmpty()) {
                for (String configName : entry.getValue()) {
                    modFavoriteObj.add(configName);
                }

                dataObj.add(entry.getKey(), modFavoriteObj);
            }
        }

        jsonObject.add("data", dataObj);
    }

    @Override
    public void loadFromJson(@NotNull JsonObject jsonObject) {
        this.filterSwitch = jsonObject.get("filter_switch").getAsBoolean();
        JsonObject dataObj = JsonUtils.getNestedObject(jsonObject, "data", false);
        this.favorites.clear();

        for (Map.Entry<String, JsonElement> entry : Objects.requireNonNull(dataObj).entrySet()) {
            Set<String> modFavoriteObj = Sets.newHashSet();
            entry.getValue().getAsJsonArray().forEach(e -> modFavoriteObj.add(e.getAsString()));
            this.favorites.put(entry.getKey(), modFavoriteObj);
        }
    }
}

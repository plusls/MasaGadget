package com.plusls.MasaGadget.game;

import com.google.common.collect.ImmutableList;
import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.util.ModId;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import com.plusls.MasaGadget.util.SearchMobSpawnPointUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.malilib.annotation.Config;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigFactory;
import top.hendrixshen.magiclib.impl.malilib.config.option.*;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;
import top.hendrixshen.magiclib.util.minecraft.InfoUtil;

import java.util.*;

public class Configs {
    private static final MagicConfigManager cm = SharedConstants.getConfigManager();
    private static final MagicConfigFactory cf = Configs.cm.getConfigFactory();

    // GENERIC
    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBoolean autoSyncEntityData = Configs.cf.newConfigBoolean("autoSyncEntityData", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBoolean cacheContainerMenu = Configs.cf.newConfigBoolean("cacheContainerMenu", true);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBoolean debug = Configs.cf.newConfigBoolean("debug", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey openConfigGui = Configs.cf.newConfigHotkey("openConfigGui", "G,C");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed renderNextRestockTime = Configs.cf.newConfigBooleanHotkeyed("renderNextRestockTime", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed renderTradeEnchantedBook = Configs.cf.newConfigBooleanHotkeyed("renderTradeEnchantedBook", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed renderVillageHomeTracer = Configs.cf.newConfigBooleanHotkeyed("renderVillageHomeTracer", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigColor renderVillageHomeTracerColor = Configs.cf.newConfigColor("renderVillageHomeTracerColor", "#500000FF");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed renderVillageJobSiteTracer = Configs.cf.newConfigBooleanHotkeyed("renderVillageJobSiteTracer", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigColor renderVillageJobSiteTracerColor = Configs.cf.newConfigColor("renderVillageJobSiteTracerColor", "#5000FF00");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed renderZombieVillagerConvertTime = Configs.cf.newConfigBooleanHotkeyed("renderZombieVillagerConvertTime", false);

    @Dependencies(require = @Dependency(ModId.minihud))
    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey searchMobSpawnPoint = Configs.cf.newConfigHotkey("searchMobSpawnPoint");

    @Dependencies(require = @Dependency(ModId.minihud))
    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigStringList searchMobSpawnPointBlackList = Configs.cf.newConfigStringList("searchMobSpawnPointBlackList", ImmutableList.of());

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey syncAllEntityData = Configs.cf.newConfigHotkey("syncAllEntityData");

    // Litematica
    @Dependencies(require = @Dependency(ModId.litematica))
    @Config(category = ConfigCategory.LITEMATICA)
    public static MagicConfigBooleanHotkeyed betterEasyPlaceMode = Configs.cf.newConfigBooleanHotkeyed("betterEasyPlaceMode", false);

    @Dependencies(require = @Dependency(ModId.litematica))
    @Config(category = ConfigCategory.LITEMATICA)
    public static MagicConfigBooleanHotkeyed disableLitematicaEasyPlaceFailTip = Configs.cf.newConfigBooleanHotkeyed("disableLitematicaEasyPlaceFailTip", false);

    @Dependencies(require = @Dependency(ModId.litematica))
    @Config(category = ConfigCategory.LITEMATICA)
    public static MagicConfigBooleanHotkeyed fixAccurateProtocol = Configs.cf.newConfigBooleanHotkeyed("fixAccurateProtocol", false);

    @Dependencies(require = {
            @Dependency(value = ModId.litematica, versionPredicates = "<0.0.0-dev.20210917.192300"),
            @Dependency(value = ModId.tweakeroo)
    })
    @Config(category = ConfigCategory.LITEMATICA)
    public static MagicConfigBoolean nudgeSelectionSupportFreeCamera = Configs.cf.newConfigBoolean("nudgeSelectionSupportFreeCamera", false);

    @Dependencies(require = @Dependency(ModId.litematica))
    @Config(category = ConfigCategory.LITEMATICA)
    public static MagicConfigBoolean saveInventoryToSchematicInServer = Configs.cf.newConfigBoolean("saveInventoryToSchematicInServer", false);

    @Dependencies(require = @Dependency(ModId.litematica))
    @Config(category = ConfigCategory.LITEMATICA)
    public static MagicConfigBoolean useRelativePath = Configs.cf.newConfigBoolean("useRelativePath", false);

    // MALILIB
    @Dependencies(require = @Dependency(value = ModId.malilib, versionPredicates = "<0.11.0"))
    @Dependencies(require = @Dependency(value = ModId.minihud, versionPredicates = "<0.20.0"))
    @Dependencies(require = @Dependency(value = ModId.tweakeroo, versionPredicates = "<0.11.1"))
    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigBoolean backportI18nSupport = Configs.cf.newConfigBoolean("backportI18nSupport", false);

    @Dependencies(require = @Dependency(ModId.mod_menu))
    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigBoolean fastSwitchMasaConfigGui = Configs.cf.newConfigBoolean("fastSwitchMasaConfigGui", false);

    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigBooleanHotkeyed favoritesSupport = Configs.cf.newConfigBooleanHotkeyed("favoritesSupport", false);

    @Dependencies(require = @Dependency(ModId.malilib))
    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigBoolean fixConfigWidgetWidth = Configs.cf.newConfigBoolean("fixConfigWidgetWidth", false);

    @Dependencies(require = @Dependency(ModId.malilib))
    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigBoolean fixConfigWidgetWidthExpand = Configs.cf.newConfigBoolean("fixConfigWidgetWidthExpand", false);

    @Dependencies(require = @Dependency(value = ModId.malilib, versionPredicates = "<0.11.0"))
    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigBoolean fixGetInventoryType = Configs.cf.newConfigBoolean("fixGetInventoryType", false);

    @Dependencies(require = @Dependency(value = ModId.malilib, versionPredicates = "<0.11.6"))
    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigBoolean fixSearchbarHotkeyInput = Configs.cf.newConfigBoolean("fixSearchbarHotkeyInput", false);

    @Dependencies(require = @Dependency(value = ModId.malilib, versionPredicates = "<0.11.0"))
    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigBoolean optimizeConfigWidgetSearch = Configs.cf.newConfigBoolean("optimizeConfigWidgetSearch", false);

    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigBoolean showOriginalConfigName = Configs.cf.newConfigBoolean("showOriginalConfigName", false);

    @Config(category = ConfigCategory.MALILIB)
    public static MagicConfigDouble showOriginalConfigNameScale = Configs.cf.newConfigDouble("showOriginalConfigNameScale", 0.65, 0, 2);

    // MiniHUD
    @Dependencies(require = @Dependency(value = ModId.minihud, versionPredicates = "<0.31.999-sakura.21"))
    @Config(category = ConfigCategory.MINIHUD)
    public static MagicConfigBoolean minihudI18n = Configs.cf.newConfigBoolean("minihudI18n", false);

    @Dependencies(require = {
            @Dependency(ModId.minihud),
            @Dependency(ModId.tweakeroo),
            @Dependency(value = ModId.minecraft, versionPredicates = ">1.14.4")
    })
    @Config(category = ConfigCategory.MINIHUD)
    public static MagicConfigBoolean pcaSyncProtocolSyncBeehive = Configs.cf.newConfigBoolean("pcaSyncProtocolSyncBeehive", false);

    // Tweakeroo
    @Dependencies(require = @Dependency(ModId.tweakeroo))
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigBoolean inventoryPreviewSupportComparator = Configs.cf.newConfigBoolean("inventoryPreviewSupportComparator", false);

    @Dependencies(require = @Dependency(ModId.tweakeroo))
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigBoolean inventoryPreviewSupportPlayer = Configs.cf.newConfigBoolean("inventoryPreviewSupportPlayer", false);

    @Dependencies(require = @Dependency(ModId.tweakeroo))
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigBoolean inventoryPreviewSupportSelect = Configs.cf.newConfigBoolean("inventoryPreviewSupportSelect", false);

    @Dependencies(require = @Dependency(ModId.tweakeroo))
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigBoolean inventoryPreviewSupportShulkerBoxItemEntity = Configs.cf.newConfigBoolean("inventoryPreviewSupportShulkerBoxItemEntity", false);

    @Dependencies(require = @Dependency(ModId.tweakeroo))
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigBoolean inventoryPreviewSupportTradeOfferList = Configs.cf.newConfigBoolean("inventoryPreviewSupportTradeOfferList", false);

    @Dependencies(require = @Dependency(ModId.tweakeroo))
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigBoolean inventoryPreviewSyncData = Configs.cf.newConfigBoolean("inventoryPreviewSyncData", false);

    @Dependencies(require = @Dependency(ModId.tweakeroo))
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigBoolean inventoryPreviewSyncDataClientOnly = Configs.cf.newConfigBoolean("inventoryPreviewSyncDataClientOnly", false);

    @Dependencies(require = @Dependency(ModId.tweakeroo))
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigBoolean inventoryPreviewUseCache = Configs.cf.newConfigBoolean("inventoryPreviewUseCache", false);

    @Dependencies(require = {
            @Dependency(ModId.tweakeroo),
            @Dependency(ModId.tweakeroo)
    })
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigBoolean restockWithCrafting = Configs.cf.newConfigBoolean("restockWithCrafting", false);

    @Dependencies(require = {
            @Dependency(ModId.tweakeroo),
            @Dependency(ModId.itemscroller)
    })
    @Config(category = ConfigCategory.TWEAKEROO)
    public static MagicConfigStringList restockWithCraftingRecipes = Configs.cf.newConfigStringList("restockWithCraftingRecipes", ImmutableList.of());

    public static void init() {
        Configs.cm.parseConfigClass(Configs.class);

        // Generic
        MagicConfigManager.setHotkeyCallback(openConfigGui, ConfigGui::openGui, true);

        Configs.searchMobSpawnPoint.getKeybind().setCallback((keyAction, iKeybind) -> {
            if (MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.minihud)) {
                SearchMobSpawnPointUtil.search();
            }

            return true;
        });

        // Litematica
        Configs.syncAllEntityData.getKeybind().setCallback((keyAction, iKeybind) -> {
            if (!PcaSyncProtocol.enable) {
                return true;
            }

            Minecraft mc = Minecraft.getInstance();

            for (Entity entity : Objects.requireNonNull(mc.level).entitiesForRendering()) {
                PcaSyncProtocol.syncEntity(entity.getId());
            }

            InfoUtil.displayChatMessage(ComponentUtil.trCompat("masa_gadget_mod.message.syncAllEntityDataSuccess")
                    .withStyle(ChatFormatting.GREEN));
            return true;
        });

        // Malilib
        Configs.favoritesSupport.setValueChangeCallback(Configs::redrawConfigGui);
        Configs.showOriginalConfigName.setValueChangeCallback(Configs::redrawConfigGui);
        Configs.showOriginalConfigNameScale.setValueChangeCallback(Configs::redrawConfigGui);
    }

    private static void redrawConfigGui(Object object) {
        ConfigGui.getCurrentInstance().ifPresent(ConfigGui::reDraw);
    }

    public static class ConfigCategory {
        public static final String GENERIC = "generic";
        public static final String LITEMATICA = "litematica";
        public static final String MALILIB = "malilib";
        public static final String MINIHUD = "minihud";
        public static final String TWEAKEROO = "tweakeroo";
    }
}

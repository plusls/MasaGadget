package com.plusls.MasaGadget.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.gui.GuiConfigs;
import com.plusls.MasaGadget.minihud.compactBborProtocol.BborProtocol;
import com.plusls.MasaGadget.tweakeroo.pcaSyncProtocol.PcaSyncProtocol;
import com.plusls.MasaGadget.util.SearchMobSpawnPointUtil;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = ModInfo.MOD_ID + ".json";
    private static final int CONFIG_VERSION = 1;

    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "generic", Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "litematica", Litematica.OPTIONS);
                ConfigUtils.readConfigBase(root, "malilib", Malilib.OPTIONS);
                ConfigUtils.readConfigBase(root, "minihud", Minihud.OPTIONS);
                ConfigUtils.readConfigBase(root, "tweakeroo", Tweakeroo.OPTIONS);
                // int version = JsonUtils.getIntegerOrDefault(root, "configVersion", 1);
            }
        }
        if (Generic.DEBUG.getBooleanValue()) {
            Configurator.setLevel(ModInfo.LOGGER.getName(), Level.toLevel("DEBUG"));
        }
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();
            ConfigUtils.writeConfigBase(root, "generic", Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "litematica", Litematica.OPTIONS);
            ConfigUtils.writeConfigBase(root, "malilib", Malilib.OPTIONS);
            ConfigUtils.writeConfigBase(root, "minihud", Minihud.OPTIONS);
            ConfigUtils.writeConfigBase(root, "tweakeroo", Tweakeroo.OPTIONS);
            root.add("configVersion", new JsonPrimitive(CONFIG_VERSION));
            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }

    public static class Generic {
        private static final String PREFIX = String.format("%s.config.generic", ModInfo.MOD_ID);
        public static final ConfigHotkey OPEN_CONFIG_GUI = new TranslatableConfigHotkey(PREFIX, "openConfigGui", "G,C");
        public static final ConfigHotkey SEARCH_MOB_SPAWN_POINT = new TranslatableConfigHotkey(PREFIX, "searchMobSpawnPoint", ";");
        public static final ConfigStringList SEARCH_MOB_SPAWN_POINT_BLACK_LIST = new TranslatableConfigStringList(PREFIX, "searchMobSpawnPointBlackList", ImmutableList.of());
        public static final ConfigHotkey SYNC_ALL_ENTITY_DATA = new TranslatableConfigHotkey(PREFIX, "syncAllEntityData", "");
        public static final ImmutableList<ConfigHotkey> HOTKEYS = ImmutableList.of(
                OPEN_CONFIG_GUI,
                SEARCH_MOB_SPAWN_POINT,
                SYNC_ALL_ENTITY_DATA
        );
        public static final ConfigBoolean DEBUG = new TranslatableConfigBoolean(PREFIX, "debug", false);
        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                OPEN_CONFIG_GUI,
                SEARCH_MOB_SPAWN_POINT,
                SEARCH_MOB_SPAWN_POINT_BLACK_LIST,
                SYNC_ALL_ENTITY_DATA,
                DEBUG
        );
        public static final List<IConfigBase> GUI_OPTIONS = new LinkedList<>(OPTIONS);

        static {
            GUI_OPTIONS.removeIf(iConfigBase -> iConfigBase == SEARCH_MOB_SPAWN_POINT && !ModInfo.isModLoaded(ModInfo.MINIHUD_MOD_ID));
            OPEN_CONFIG_GUI.getKeybind().setCallback((keyAction, iKeybind) -> {
                GuiBase.openGui(new GuiConfigs());
                return true;
            });
            SEARCH_MOB_SPAWN_POINT.getKeybind().setCallback((keyAction, iKeybind) -> {
                if (ModInfo.isModLoaded(ModInfo.MINIHUD_MOD_ID)) {
                    SearchMobSpawnPointUtil.search();
                }
                return true;
            });
            DEBUG.setValueChangeCallback(config -> {
                if (config.getBooleanValue()) {
                    Configurator.setLevel(ModInfo.LOGGER.getName(), Level.toLevel("DEBUG"));
                } else {
                    Configurator.setLevel(ModInfo.LOGGER.getName(), Level.toLevel("INFO"));
                }
            });
            SYNC_ALL_ENTITY_DATA.getKeybind().setCallback((keyAction, iKeybind) -> {
                if (!PcaSyncProtocol.enable) {
                    return true;
                }
                MinecraftClient mc = MinecraftClient.getInstance();
                for (Entity entity : Objects.requireNonNull(mc.world).getEntities()) {
                    PcaSyncProtocol.syncEntity(entity.getId());
                }
                TranslatableText text = new TranslatableText("masa_gadget_mod.message.syncAllEntityDataSuccess");
                text.setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GREEN)));
                Objects.requireNonNull(mc.player).sendMessage(text, false);
                return true;
            });
        }
    }

    public static class Litematica {
        private static final String PREFIX = String.format("%s.config.litematica", ModInfo.MOD_ID);
        public static final ConfigBoolean BETTER_EASY_PLACE_MODE = new TranslatableConfigBoolean(PREFIX, "betterEasyPlaceMode", false);
        public static final ConfigBoolean DISABLE_LITEMATICA_EASY_PLACE_FAIL_TIP = new TranslatableConfigBoolean(PREFIX, "disableLitematicaEasyPlaceFailTip", false);
        public static final ConfigBoolean FIX_ACCURATE_PROTOCOL = new TranslatableConfigBoolean(PREFIX, "fixAccurateProtocol", true);
        public static final ConfigBoolean SAVE_INVENTORY_TO_SCHEMATIC_IN_SERVER = new TranslatableConfigBoolean(PREFIX, "saveInventoryToSchematicInServer", false);
        public static final ConfigBoolean USE_RELATIVE_PATH = new TranslatableConfigBoolean(PREFIX, "useRelativePath", false);

        public static final List<IConfigBase> OPTIONS = ImmutableList.of(
                BETTER_EASY_PLACE_MODE,
                FIX_ACCURATE_PROTOCOL,
                DISABLE_LITEMATICA_EASY_PLACE_FAIL_TIP,
                SAVE_INVENTORY_TO_SCHEMATIC_IN_SERVER,
                USE_RELATIVE_PATH
        );

        public static final List<IConfigBase> GUI_OPTIONS = new LinkedList<>(OPTIONS);

    }

    public static class Malilib {
        private static final String PREFIX = String.format("%s.config.malilib", ModInfo.MOD_ID);
        public static final ConfigBoolean FAST_SWITCH_MASA_CONFIG_GUI = new TranslatableConfigBoolean(PREFIX, "fastSwitchMasaConfigGui", true);
        public static final ConfigBoolean SHOW_ORIGINAL_CONFIG_NAME = new TranslatableConfigBoolean(PREFIX, "showOriginalConfigName", true);
        public static final ConfigBoolean FIX_CONFIG_WIDGET_WIDTH = new TranslatableConfigBoolean(PREFIX, "fixConfigWidgetWidth", true);
        public static final ConfigBoolean FIX_GET_INVENTORY_TYPE = new TranslatableConfigBoolean(PREFIX, "fixGetInventoryType", true);
        public static final ConfigBoolean OPTIMIZE_CONFIG_WIDGET_SEARCH = new TranslatableConfigBoolean(PREFIX, "optimizeConfigWidgetSearch", true);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                FAST_SWITCH_MASA_CONFIG_GUI,
                FIX_CONFIG_WIDGET_WIDTH,
                FIX_GET_INVENTORY_TYPE,
                OPTIMIZE_CONFIG_WIDGET_SEARCH,
                SHOW_ORIGINAL_CONFIG_NAME
        );

        public static final List<IConfigBase> GUI_OPTIONS = new LinkedList<>(OPTIONS);

        static {
            GUI_OPTIONS.removeIf(iConfigBase -> iConfigBase == FAST_SWITCH_MASA_CONFIG_GUI && !ModInfo.isModLoaded(ModInfo.MODMENU_MOD_ID));
        }
    }

    public static class Minihud {
        private static final String PREFIX = String.format("%s.config.minihud", ModInfo.MOD_ID);
        public static final ConfigBoolean COMPACT_BBOR_PROTOCOL = new TranslatableConfigBoolean(PREFIX, "compactBborProtocol", true);
        public static final ConfigBoolean PCA_SYNC_PROTOCOL_SYNC_BEEHIVE = new TranslatableConfigBoolean(PREFIX, "pcaSyncProtocolSyncBeehive", true);
        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                COMPACT_BBOR_PROTOCOL,
                PCA_SYNC_PROTOCOL_SYNC_BEEHIVE
        );

        public static final List<IConfigBase> GUI_OPTIONS = new LinkedList<>(OPTIONS);

        static {
            GUI_OPTIONS.removeIf(iConfigBase -> iConfigBase == PCA_SYNC_PROTOCOL_SYNC_BEEHIVE && !ModInfo.isModLoaded(ModInfo.TWEAKEROO_MOD_ID));
            COMPACT_BBOR_PROTOCOL.setValueChangeCallback(config -> {
                if (config.getBooleanValue()) {
                    ClientWorld world = MinecraftClient.getInstance().world;
                    if (world == null) {
                        return;
                    }
                    BborProtocol.bborInit(world.getRegistryKey().getValue());
                }
            });
        }

    }

    public static class Tweakeroo {
        private static final String PREFIX = String.format("%s.config.tweakeroo", ModInfo.MOD_ID);
        public static final ConfigBoolean AUTO_SYNC_TRADE_OFFER_LIST = new TranslatableConfigBoolean(PREFIX, "autoSyncTradeOfferList", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_COMPARATOR = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportComparator", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_LARGE_BARREL = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportLargeBarrel", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_PLAYER = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportPlayer", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_SELECT = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportSelect", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_SHULKER_BOX_ITEM_ENTITY = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportShulkerBoxItemEntity", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_TRADE_OFFER_LIST = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportTradeOfferList", true);
        public static final ConfigBoolean PCA_SYNC_PROTOCOL = new TranslatableConfigBoolean(PREFIX, "pcaSyncProtocol", true);
        public static final ConfigBoolean RENDER_NEXT_RESTOCK_TIME = new TranslatableConfigBoolean(PREFIX, "renderNextRestockTime", false);
        public static final ConfigBoolean RENDER_TRADE_ENCHANTED_BOOK = new TranslatableConfigBoolean(PREFIX, "renderTradeEnchantedBook", false);
        public static final ConfigBoolean RENDER_ZOMBIE_VILLAGER_CONVERT_TIME = new TranslatableConfigBoolean(PREFIX, "renderZombieVillagerConvertTime", false);
        public static final ConfigBoolean RESTOCK_WITH_CRAFTING = new TranslatableConfigBoolean(PREFIX, "restockWithCrafting", false);
        public static final ConfigStringList RESTOCK_WITH_CRAFTING_RECIPES = new TranslatableConfigStringList(PREFIX, "restockWithCraftingRecipes", ImmutableList.of());

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                AUTO_SYNC_TRADE_OFFER_LIST,
                INVENTORY_PREVIEW_SUPPORT_COMPARATOR,
                INVENTORY_PREVIEW_SUPPORT_LARGE_BARREL,
                INVENTORY_PREVIEW_SUPPORT_PLAYER,
                INVENTORY_PREVIEW_SUPPORT_SELECT,
                INVENTORY_PREVIEW_SUPPORT_SHULKER_BOX_ITEM_ENTITY,
                INVENTORY_PREVIEW_SUPPORT_TRADE_OFFER_LIST,
                PCA_SYNC_PROTOCOL,
                RENDER_NEXT_RESTOCK_TIME,
                RENDER_TRADE_ENCHANTED_BOOK,
                RENDER_ZOMBIE_VILLAGER_CONVERT_TIME,
                RESTOCK_WITH_CRAFTING,
                RESTOCK_WITH_CRAFTING_RECIPES
        );

        public static final List<IConfigBase> GUI_OPTIONS = new LinkedList<>(OPTIONS);

        static {
            GUI_OPTIONS.removeIf(iConfigBase -> iConfigBase == INVENTORY_PREVIEW_SUPPORT_LARGE_BARREL && !ModInfo.isModLoaded(ModInfo.CARPET_TIS_ADDITION_MOD_ID));
        }
    }
}
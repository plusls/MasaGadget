package com.plusls.MasaGadget.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.gui.GuiConfigs;
import com.plusls.MasaGadget.minihud.compactBborProtocol.BborProtocol;
import com.plusls.MasaGadget.mixin.litematica.LitematicaDependencyUtil;
import com.plusls.MasaGadget.mixin.tweakeroo.TweakerooDependencyUtil;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = ModInfo.MOD_ID + ".json";
    private static final int CONFIG_VERSION = 1;

    public static class Generic {
        private static final String PREFIX = String.format("%s.config.generic", ModInfo.MOD_ID);
        public static final ConfigHotkey OPEN_CONFIG_GUI = new TranslatableConfigHotkey(PREFIX, "openConfigGui", "G,C");
        public static final ConfigBoolean DEBUG = new TranslatableConfigBoolean(PREFIX, "debug", false);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                OPEN_CONFIG_GUI,
                DEBUG
        );

        public static final ImmutableList<ConfigHotkey> HOTKEYS = ImmutableList.of(
                OPEN_CONFIG_GUI
        );

        static {
            OPEN_CONFIG_GUI.getKeybind().setCallback((keyAction, iKeybind) -> {
                GuiBase.openGui(new GuiConfigs());
                return true;
            });
            DEBUG.setValueChangeCallback(config -> {
                if (config.getBooleanValue()) {
                    Configurator.setLevel(ModInfo.LOGGER.getName(), Level.toLevel("DEBUG"));
                } else {
                    Configurator.setLevel(ModInfo.LOGGER.getName(), Level.toLevel("INFO"));
                }
            });
        }
    }

    public static class Litematica {
        private static final String PREFIX = String.format("%s.config.litematica", ModInfo.MOD_ID);
        public static final ConfigBoolean FIX_ACCURATE_PROTOCOL = new TranslatableConfigBoolean(PREFIX, "fixAccurateProtocol", true);
        public static final ConfigBoolean NUDGE_SELECTION_SUPPORT_FREE_CAMERA = new TranslatableConfigBoolean(PREFIX, "nudgeSelectionSupportFreeCamera", true);
        public static final ConfigBoolean SAVE_INVENTORY_TO_SCHEMATIC_IN_SERVER = new TranslatableConfigBoolean(PREFIX, "saveInventoryToSchematicInServer", false);
        public static final ConfigBoolean USE_RELATIVE_PATH = new TranslatableConfigBoolean(PREFIX, "useRelativePath", false);

        public static final List<IConfigBase> OPTIONS = ImmutableList.of(
                FIX_ACCURATE_PROTOCOL,
                NUDGE_SELECTION_SUPPORT_FREE_CAMERA,
                SAVE_INVENTORY_TO_SCHEMATIC_IN_SERVER,
                USE_RELATIVE_PATH
        );

        public static final List<IConfigBase> GUI_OPTIONS = new LinkedList<>(OPTIONS);

        static {
            GUI_OPTIONS.removeIf(iConfigBase -> {
                if (iConfigBase == NUDGE_SELECTION_SUPPORT_FREE_CAMERA &&
                        (MasaGadgetMixinPlugin.checkDependency(MasaGadgetMixinPlugin.LITEMATICA_MOD_ID,
                                ">=" + LitematicaDependencyUtil.NUDGE_SELECTION_SUPPORT_FREECAMERA_BREAK_VERSION) ||
                                !MasaGadgetMixinPlugin.isTweakerooLoaded)) {
                    return true;
                }
                return false;
            });
        }
    }

    public static class Malilib {
        private static final String PREFIX = String.format("%s.config.malilib", ModInfo.MOD_ID);
        public static final ConfigBoolean FIX_CONFIG_WIDGET_WIDTH = new TranslatableConfigBoolean(PREFIX, "fixConfigWidgetWidth", true);
        public static final ConfigBoolean FIX_GET_INVENTORY_TYPE = new TranslatableConfigBoolean(PREFIX, "fixGetInventoryType", true);
        public static final ConfigBoolean OPTIMIZE_CONFIG_WIDGET_SEARCH = new TranslatableConfigBoolean(PREFIX, "optimizeConfigWidgetSearch", true);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                FIX_CONFIG_WIDGET_WIDTH,
                OPTIMIZE_CONFIG_WIDGET_SEARCH
        );
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
            GUI_OPTIONS.removeIf(iConfigBase -> {
                if (iConfigBase == PCA_SYNC_PROTOCOL_SYNC_BEEHIVE && !MasaGadgetMixinPlugin.isTweakerooLoaded) {
                    return true;
                }
                return false;
            });
            COMPACT_BBOR_PROTOCOL.setValueChangeCallback(config -> {
                if (config.getBooleanValue()) {
                    BborProtocol.bborInit(DimensionType.getId(Objects.requireNonNull(MinecraftClient.getInstance().world).getDimension().getType()));
                }
            });
        }

    }

    public static class Tweakeroo {
        private static final String PREFIX = String.format("%s.config.tweakeroo", ModInfo.MOD_ID);
        public static final ConfigBoolean AUTO_SYNC_TRADE_OFFER_LIST = new TranslatableConfigBoolean(PREFIX, "autoSyncTradeOfferList", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_FREE_CAMERA = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportFreeCamera", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_PLAYER = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportPlayer", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_SELECT = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportSelect", true);
        public static final ConfigBoolean INVENTORY_PREVIEW_SUPPORT_TRADE_OFFER_LIST = new TranslatableConfigBoolean(PREFIX, "inventoryPreviewSupportTradeOfferList", true);
        public static final ConfigBoolean PCA_SYNC_PROTOCOL = new TranslatableConfigBoolean(PREFIX, "pcaSyncProtocol", true);
        public static final ConfigBoolean RENDER_TRADE_ENCHANTED_BOOK = new TranslatableConfigBoolean(PREFIX, "renderTradeEnchantedBook", false);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                AUTO_SYNC_TRADE_OFFER_LIST,
                INVENTORY_PREVIEW_SUPPORT_FREE_CAMERA,
                INVENTORY_PREVIEW_SUPPORT_PLAYER,
                INVENTORY_PREVIEW_SUPPORT_SELECT,
                INVENTORY_PREVIEW_SUPPORT_TRADE_OFFER_LIST,
                PCA_SYNC_PROTOCOL,
                RENDER_TRADE_ENCHANTED_BOOK
        );

        public static final List<IConfigBase> GUI_OPTIONS = new LinkedList<>(OPTIONS);

        static {
            GUI_OPTIONS.removeIf(iConfigBase -> {
                if (iConfigBase == INVENTORY_PREVIEW_SUPPORT_FREE_CAMERA &&
                        MasaGadgetMixinPlugin.checkDependency(MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID,
                                ">=" + TweakerooDependencyUtil.INVENTORY_PREVIEW_SUPPORT_FREE_CAMERA_BREAK_VERSION)) {
                    return true;
                }
                return false;
            });
        }
    }

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
                int version = JsonUtils.getIntegerOrDefault(root, "configVersion", 1);
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
}
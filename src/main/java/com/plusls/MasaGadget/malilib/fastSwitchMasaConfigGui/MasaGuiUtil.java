package com.plusls.MasaGadget.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.util.ModMenuApiMarker;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class MasaGuiUtil {
    private final static Map<ConfigScreenFactory<?>, String> masaGuiData = new HashMap<>();
    private final static Map<Class<?>, ConfigScreenFactory<?>> masaGuiClassData = new HashMap<>();
    private static boolean initialised = false;

    public static Map<ConfigScreenFactory<?>, String> getMasaGuiData() {
        if (!initialised) {
            initMasaModScreenList();
        }
        return masaGuiData;
    }

    public static Map<Class<?>, ConfigScreenFactory<?>> getMasaGuiClassData() {
        if (!initialised) {
            initMasaModScreenList();
        }
        return masaGuiClassData;
    }


    private static void initMasaModScreenList() {
        initialised = true;
        MinecraftClient client = MinecraftClient.getInstance();
        if (!ModInfo.isModLoaded(ModInfo.MODMENU_MOD_ID)) {
            return;
        }
        FabricLoader.getInstance().getEntrypointContainers("modmenu", ModMenuApiMarker.class).forEach(entrypoint -> {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            try {
                ModMenuApiMarker marker = entrypoint.getEntrypoint();
                if (marker instanceof ModMenuApi) {
                    ModMenuApi api = (ModMenuApi) marker;
                    Screen screen = api.getModConfigScreenFactory().create(client.currentScreen);
                    if (screen instanceof GuiConfigsBase) {
                        ConfigScreenFactory<?> configScreenFactory = api.getModConfigScreenFactory();
                        masaGuiData.put(configScreenFactory, metadata.getName());
                        masaGuiClassData.put(screen.getClass(), configScreenFactory);
                    }
                } else if (marker instanceof io.github.prospector.modmenu.api.ModMenuApi) {
                    /* Legacy API */
                    io.github.prospector.modmenu.api.ModMenuApi api = (io.github.prospector.modmenu.api.ModMenuApi) entrypoint.getEntrypoint();
                    Screen screen = api.getModConfigScreenFactory().create(client.currentScreen);
                    if (screen instanceof GuiConfigsBase) {
                        ConfigScreenFactory<?> configScreenFactory = api.getModConfigScreenFactory();
                        masaGuiData.put(configScreenFactory, metadata.getName());
                        masaGuiClassData.put(screen.getClass(), configScreenFactory);
                    }
                }
            } catch (Throwable e) {
                ModInfo.LOGGER.error("Mod {} provides a broken implementation of ModMenuApi", metadata.getId(), e);
            }
        });
    }
}

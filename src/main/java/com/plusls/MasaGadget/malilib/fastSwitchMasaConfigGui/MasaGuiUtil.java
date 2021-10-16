package com.plusls.MasaGadget.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.util.ModMenuApiMarker;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;

public class MasaGuiUtil {
    public static Map<ConfigScreenFactory<?>, String> masaGuiData = new HashMap<>();
    public static Map<Class<?>, ConfigScreenFactory<?>> masaGuiClassData = new HashMap<>();

    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register(MasaGuiUtil::initMasaModScreenList);
    }

    public static void initMasaModScreenList(MinecraftClient client) {

        FabricLoader.getInstance().getEntrypointContainers("modmenu", ModMenuApiMarker.class).forEach(entrypoint -> {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            try {
                ModMenuApiMarker marker = entrypoint.getEntrypoint();
                if (marker instanceof ModMenuApi) {
                    ModMenuApi api = (ModMenuApi) marker;
                    Screen screen = api.getModConfigScreenFactory().create(client.currentScreen);
                    if (screen instanceof GuiConfigsBase) {
                        masaGuiData.put(api.getModConfigScreenFactory(), metadata.getName());
                        masaGuiClassData.put(screen.getClass(), api.getModConfigScreenFactory());
                    }
                }  else if (marker instanceof io.github.prospector.modmenu.api.ModMenuApi) {
                    /* Legacy API */
                    io.github.prospector.modmenu.api.ModMenuApi api = (io.github.prospector.modmenu.api.ModMenuApi) entrypoint.getEntrypoint();
                    Screen screen = api.getModConfigScreenFactory().create(client.currentScreen);
                    if (screen instanceof GuiConfigsBase) {
                        masaGuiData.put(api.getModConfigScreenFactory(), metadata.getName());
                        masaGuiClassData.put(screen.getClass(), api.getModConfigScreenFactory());
                    }
                }
            } catch (Throwable e) {
                ModInfo.LOGGER.error("Mod {} provides a broken implementation of ModMenuApi", metadata.getId(), e);
            }
        });
    }
}

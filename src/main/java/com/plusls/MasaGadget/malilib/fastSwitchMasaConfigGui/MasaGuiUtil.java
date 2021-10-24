package com.plusls.MasaGadget.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
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

        FabricLoader.getInstance().getEntrypointContainers("modmenu", ModMenuApi.class).forEach(provider -> {
            ModMenuApi entrypoint = provider.getEntrypoint();

            ModMetadata metadata = provider.getProvider().getMetadata();
            try {
                Screen screen = entrypoint.getModConfigScreenFactory().create(client.currentScreen);
                if (screen instanceof GuiConfigsBase) {
                    ConfigScreenFactory<?> configScreenFactory = entrypoint.getModConfigScreenFactory();
                    masaGuiData.put(configScreenFactory, metadata.getName());
                    masaGuiClassData.put(screen.getClass(), configScreenFactory);
                }

            } catch (Throwable e) {
                ModInfo.LOGGER.error("Mod {} provides a broken implementation of ModMenuApi", metadata.getId(), e);
            }
        });
    }
}

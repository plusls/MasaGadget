package com.plusls.MasaGadget.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MasaGuiUtil {
    public final static Map<ConfigScreenFactory<?>, String> masaGuiData = new HashMap<>();
    public final static ArrayList<ConfigScreenFactory<?>> masaGuiConfigScreenFactorys = new ArrayList<>();
    public final static Map<Class<?>, ConfigScreenFactory<?>> masaGuiClassData = new HashMap<>();

    private static boolean initialised = false;

    public static void initMasaModScreenList() {
        if (initialised) {
            return;
        }
        initialised = true;
        MinecraftClient client = MinecraftClient.getInstance();
        if (!ModInfo.isModLoaded(ModInfo.MODMENU_MOD_ID)) {
            return;
        }
        FabricLoader.getInstance().getEntrypointContainers("modmenu", ModMenuApi.class).forEach(provider -> {
            ModMenuApi entrypoint = provider.getEntrypoint();

            ModMetadata metadata = provider.getProvider().getMetadata();
            try {
                Screen screen = entrypoint.getModConfigScreenFactory().create(client.currentScreen);
                if (screen instanceof GuiConfigsBase) {
                    ConfigScreenFactory<?> configScreenFactory = entrypoint.getModConfigScreenFactory();
                    masaGuiData.put(configScreenFactory, metadata.getName());
                    masaGuiConfigScreenFactorys.add(configScreenFactory);
                    masaGuiClassData.put(screen.getClass(), configScreenFactory);
                }
            } catch (Throwable e) {
                ModInfo.LOGGER.error("Mod {} provides a broken implementation of ModMenuApi", metadata.getId(), e);
            }
        });
    }
}

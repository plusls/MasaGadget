package com.plusls.MasaGadget.malilib.fastSwitchMasaConfigGui;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.compat.modmenu.ConfigScreenFactoryCompat;
import com.plusls.MasaGadget.util.MiscUtil;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MasaGuiUtil {
    public final static Map<ConfigScreenFactoryCompat<?>, String> masaGuiData = new HashMap<>();
    public final static ArrayList<ConfigScreenFactoryCompat<?>> masaGuiConfigScreenFactorys = new ArrayList<>();
    public final static Map<Class<?>, ConfigScreenFactoryCompat<?>> masaGuiClassData = new HashMap<>();

    @Nullable
    private static final Class<?> modMenuApiClass;
    private static final Method getModConfigScreenFactoryMethod;
    private static final Method createMethod;
    @Nullable
    private static final Class<?> legacyModMenuApiClass;
    private static final Method legacyGetModConfigScreenFactoryMethod;
    private static final Method legacyCreateMethod;
    private static final Method legacyGetConfigScreenFactory;
    private static boolean initialized = false;

    static {
        Class<?> tmpModMenuApiClass;
        Method tmpGetModConfigScreenFactoryMethod;
        Method tmpGetConfigScreenFactory;

        Method tmpCreateMethod;

        try {
            tmpModMenuApiClass = Class.forName("com.terraformersmc.modmenu.api.ModMenuApi");
            tmpGetModConfigScreenFactoryMethod = tmpModMenuApiClass.getMethod("getModConfigScreenFactory");
            Class<?> configScreenFactoryClass = Class.forName("com.terraformersmc.modmenu.api.ConfigScreenFactory");
            tmpCreateMethod = configScreenFactoryClass.getMethod("create", Screen.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            tmpModMenuApiClass = null;
            tmpGetModConfigScreenFactoryMethod = null;
            tmpCreateMethod = null;
        }
        modMenuApiClass = tmpModMenuApiClass;
        getModConfigScreenFactoryMethod = tmpGetModConfigScreenFactoryMethod;
        createMethod = tmpCreateMethod;

        try {
            tmpModMenuApiClass = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
            try {
                tmpGetModConfigScreenFactoryMethod = tmpModMenuApiClass.getMethod("getModConfigScreenFactory");
                Class<?> legacyConfigScreenFactoryClass = Class.forName("io.github.prospector.modmenu.api.ConfigScreenFactory");
                tmpCreateMethod = legacyConfigScreenFactoryClass.getMethod("create", Screen.class);
                tmpGetConfigScreenFactory = null;
            } catch (NoSuchMethodException e) {
                tmpGetConfigScreenFactory = tmpModMenuApiClass.getMethod("getConfigScreenFactory");
                tmpGetModConfigScreenFactoryMethod = null;
                tmpCreateMethod = null;
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            tmpModMenuApiClass = null;
            tmpGetModConfigScreenFactoryMethod = null;
            tmpCreateMethod = null;
            tmpGetConfigScreenFactory = null;
        }
        legacyModMenuApiClass = tmpModMenuApiClass;
        legacyGetModConfigScreenFactoryMethod = tmpGetModConfigScreenFactoryMethod;
        legacyCreateMethod = tmpCreateMethod;
        legacyGetConfigScreenFactory = tmpGetConfigScreenFactory;
    }

    public static void initMasaModScreenList() {
        if (initialized) {
            return;
        }
        initialized = true;
        Minecraft client = Minecraft.getInstance();
        if (!ModInfo.isModLoaded(ModInfo.MODMENU_MOD_ID)) {
            return;
        }
        FabricLoader.getInstance().getEntrypointContainers("modmenu", Object.class).forEach(entrypoint -> {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            try {
                Object api = entrypoint.getEntrypoint();
                ConfigScreenFactoryCompat<?> configScreenFactoryCompat;
                if (modMenuApiClass != null && modMenuApiClass.isAssignableFrom(api.getClass())) {
                    // >= 1.16
                    Object modConfigScreenFactory = getModConfigScreenFactoryMethod.invoke(api);
                    configScreenFactoryCompat = screen -> {
                        try {
                            return (Screen) createMethod.invoke(modConfigScreenFactory, screen);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    };

                } else if (legacyModMenuApiClass != null && legacyModMenuApiClass.isAssignableFrom(api.getClass())) {
                    if (legacyGetModConfigScreenFactoryMethod != null) {
                        // 1.15 and 1.16 legacy
                        Object legacyModConfigScreenFactory = legacyGetModConfigScreenFactoryMethod.invoke(api);
                        configScreenFactoryCompat = screen -> {
                            try {
                                return (Screen) legacyCreateMethod.invoke(legacyModConfigScreenFactory, screen);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        };
                    } else {
                        // 1.14
                        Function<Screen, ? extends Screen> f = MiscUtil.cast(legacyGetConfigScreenFactory.invoke(api));
                        configScreenFactoryCompat = f::apply;
                    }

                } else {
                    ModInfo.LOGGER.error("Mod {} provides a unknow type {} of ModMenuApi", metadata.getId(), api.getClass());
                    return;
                }
                Screen screen = configScreenFactoryCompat.create(client.screen);
                if (screen instanceof GuiConfigsBase) {
                    String modName = metadata.getName();
                    if (!masaGuiClassData.containsKey(screen.getClass())) {
                        masaGuiData.put(configScreenFactoryCompat, modName);
                        masaGuiConfigScreenFactorys.add(configScreenFactoryCompat);
                        masaGuiClassData.put(screen.getClass(), configScreenFactoryCompat);
                    } else {
                        ConfigScreenFactoryCompat<?> savedConfigScreenFactoryCompat = masaGuiClassData.get(screen.getClass());
                        String savedName = masaGuiData.get(savedConfigScreenFactoryCompat);
                        if (savedName.length() > modName.length()) {
                            masaGuiData.put(savedConfigScreenFactoryCompat, modName);
                        }
                    }

                }
            } catch (Throwable e) {
                ModInfo.LOGGER.error("Mod {} provides a broken implementation of ModMenuApi", metadata.getId(), e);
            }
        });
    }
}

package com.plusls.MasaGadget.impl.mod_tweak.malilib.fastSwitchMasaConfigGui;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.interfaces.IStringValue;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.compat.modmenu.ModMenuApiCompat;
import top.hendrixshen.magiclib.util.ReflectionUtil;
import top.hendrixshen.magiclib.util.collect.ValueContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FastMasaGuiSwitcher {
    @Getter(lazy = true)
    private static final FastMasaGuiSwitcher instance = new FastMasaGuiSwitcher();

    private final BiMap<ModMenuApiCompat.ConfigScreenFactoryCompat<?>, IStringValue> guiModName = HashBiMap.create();
    private final Map<Class<?>, ModMenuApiCompat.ConfigScreenFactoryCompat<?>> guiClass = Maps.newHashMap();

    private final ValueContainer<Class<?>> modMenuApiClass;
    private final ValueContainer<Method> getModConfigScreenFactoryMethod;
    private final ValueContainer<Method> createMethod;
    private final ValueContainer<Class<?>> legacyModMenuApiClass;
    private final ValueContainer<Method> legacyGetModConfigScreenFactoryMethod;
    private final ValueContainer<Method> legacyCreateMethod;
    private final ValueContainer<Method> legacyGetConfigScreenFactory;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private FastMasaGuiSwitcher() {
        this.modMenuApiClass = ReflectionUtil.getClass("com.terraformersmc.modmenu.api.ModMenuApi");
        this.getModConfigScreenFactoryMethod = ReflectionUtil.getMethod(this.modMenuApiClass, "getModConfigScreenFactory");
        this.createMethod = ReflectionUtil.getMethod("com.terraformersmc.modmenu.api.ConfigScreenFactory", "create", Screen.class);
        this.legacyModMenuApiClass = ReflectionUtil.getClass("io.github.prospector.modmenu.api.ModMenuApi");
        this.legacyGetConfigScreenFactory = ReflectionUtil.getMethod(this.legacyModMenuApiClass, "getModConfigScreenFactory");
        this.legacyCreateMethod = ReflectionUtil.getMethod("io.github.prospector.modmenu.api.ConfigScreenFactory", "create", Screen.class);
        this.legacyGetModConfigScreenFactoryMethod = ReflectionUtil.getMethod(this.legacyModMenuApiClass, "getConfigScreenFactory");
    }

    @ApiStatus.Internal
    public void init() {
        if (this.initialized.get()) {
            throw new IllegalStateException("Re-trigger initialize.");
        }

        if (!MagicLib.getInstance().getCurrentPlatform().isModLoaded(ModId.mod_menu)) {
            return;
        }

        Minecraft client = Minecraft.getInstance();

        // TODO: Platform api
        FabricLoader.getInstance().getEntrypointContainers("modmenu", Object.class).forEach(entrypoint -> {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            try {
                Object api = entrypoint.getEntrypoint();
                ModMenuApiCompat.ConfigScreenFactoryCompat<?> configScreenFactoryCompat;

                if (this.modMenuApiClass.isPresent() && this.modMenuApiClass.get().isAssignableFrom(api.getClass()) &&
                        this.getModConfigScreenFactoryMethod.isPresent() && this.createMethod.isPresent()) {
                    // >= 1.16
                    Object modConfigScreenFactory = this.getModConfigScreenFactoryMethod.get().invoke(api);

                    configScreenFactoryCompat = screen -> {
                        try {
                            return (Screen) this.createMethod.get().invoke(modConfigScreenFactory, screen);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    };
                } else if (this.legacyModMenuApiClass.isPresent() && this.legacyModMenuApiClass.get().isAssignableFrom(api.getClass())) {
                    if (this.legacyGetModConfigScreenFactoryMethod.isPresent()) {
                        // 1.15 and 1.16 legacy
                        Object legacyModConfigScreenFactory = this.legacyGetModConfigScreenFactoryMethod.get().invoke(api);

                        configScreenFactoryCompat = screen -> {
                            try {
                                return (Screen) this.legacyCreateMethod.get().invoke(legacyModConfigScreenFactory, screen);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        };
                    } else {
                        // 1.14
                        Function<Screen, ? extends Screen> f = MiscUtil.cast(this.legacyGetConfigScreenFactory.get().invoke(api));
                        configScreenFactoryCompat = f::apply;
                    }
                } else {
                    SharedConstants.getLogger().error("Mod {} provides a unknown type {} of ModMenuApi", metadata.getId(), api.getClass());
                    return;
                }

                Screen screen = configScreenFactoryCompat.create(client.screen);

                if (!(screen instanceof GuiConfigsBase)) {
                    return;
                }

                String modName = metadata.getName();

                if (!this.guiClass.containsKey(screen.getClass())) {
                    this.guiModName.put(configScreenFactoryCompat, () -> modName);
                    this.guiClass.put(screen.getClass(), configScreenFactoryCompat);
                } else {
                    ModMenuApiCompat.ConfigScreenFactoryCompat<?> savedConfigScreenFactoryCompat = this.guiClass.get(screen.getClass());
                    String savedName = guiModName.get(savedConfigScreenFactoryCompat).getStringValue();

                    if (savedName.length() > modName.length()) {
                        this.guiModName.put(savedConfigScreenFactoryCompat, () -> modName);
                    }
                }
            } catch (Throwable e) {
                SharedConstants.getLogger().error("Mod {} provides a broken implementation of ModMenuApi", metadata.getId(), e);
            }
        });

        this.initialized.set(true);
    }

    public List<IStringValue> getModNameList() {
        return this.guiModName.values()
                .stream()
                .sorted(Comparator.comparing(IStringValue::getStringValue))
                .collect(Collectors.toList());
    }

    public IStringValue getModName(Class<?> clazz) {
        return this.getModName(this.getConfigScreenFactory(clazz));
    }

    public IStringValue getModName(ModMenuApiCompat.ConfigScreenFactoryCompat<?> configScreenFactory) {
        return this.guiModName.get(configScreenFactory);
    }

    public ModMenuApiCompat.ConfigScreenFactoryCompat<?> getConfigScreenFactory(Class<?> clazz) {
        return this.guiClass.get(clazz);
    }

    public ModMenuApiCompat.ConfigScreenFactoryCompat<?> getConfigScreenFactory(IStringValue modName) {
        return this.guiModName.inverse().get(modName);
    }
}

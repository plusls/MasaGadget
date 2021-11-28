package com.plusls.MasaGadget;

import com.plusls.MasaGadget.mixin.CustomDepPredicate;
import com.plusls.MasaGadget.mixin.Dependencies;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MasaGadgetMixinPlugin implements IMixinConfigPlugin {

    static private Method oldMatchesMethod;
    static private Method oldDisplayCriticalErrorMethod;

    static {
        try {
            oldMatchesMethod = Class.forName("net.fabricmc.loader.util.version.VersionPredicateParser").getMethod("matches", Version.class, String.class);
            oldDisplayCriticalErrorMethod = Class.forName("net.fabricmc.loader.gui.FabricGuiEntry").getMethod("displayCriticalError", Throwable.class, boolean.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }
    }

    private final List<String> obfuscatedMixinList = new ArrayList<>();

    private static ClassNode loadClassNode(String className) {
        ClassNode classNode;
        try {
            classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalStateException(String.format("load ClassNode: %s fail.", className));
        }
        return classNode;
    }

    private static boolean myMatches(Version version, String s) {
        try {
            if (oldMatchesMethod != null) {
                return (boolean) oldMatchesMethod.invoke(null, version, s);
            }
            return VersionPredicateParser.parse(s).test(version);
        } catch (VersionParsingException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void myDisplayCriticalError(Throwable exception) {
        if (oldDisplayCriticalErrorMethod != null) {
            try {
                oldDisplayCriticalErrorMethod.invoke(null, exception, true);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            FabricGuiEntry.displayCriticalError(exception, true);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean checkDependency(String modId, String version) {
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(modId);
        if (modContainerOptional.isPresent()) {
            ModContainer modContainer = modContainerOptional.get();
            return myMatches(modContainer.getMetadata().getVersion(), version);
        }
        return false;
    }

    @Override
    public void onLoad(String mixinPackage) {
        FabricLoader.getInstance().getModContainer(ModInfo.MOD_ID).ifPresent(
                container -> container.getMetadata().getCustomValue("compat").getAsObject().forEach(
                        customValue -> {
                            if (ModInfo.isModLoaded(customValue.getKey()) &&
                                    !checkDependency(customValue.getKey(), customValue.getValue().getAsString())) {
                                myDisplayCriticalError(new IllegalStateException(String.format("Mod %s requires: %s",
                                        customValue.getKey(), customValue.getValue().getAsString())));
                            }
                        }
                )
        );
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        ClassNode mixinClassNode = loadClassNode(mixinClassName);
        return checkDependencies(mixinClassNode, targetClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return obfuscatedMixinList;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    private boolean checkDependency(String targetClassName, AnnotationNode dependency) {
        String modId = Annotations.getValue(dependency, "modId");
        List<String> versionList = Annotations.getValue(dependency, "version");

        for (String version : versionList) {
            if (!checkDependency(modId, version)) {
                return false;
            }
        }

        ClassNode targetClassNode = loadClassNode(targetClassName);
        List<Type> predicateList = Annotations.getValue(dependency, "predicate");
        if (predicateList != null) {
            for (Type predicateType : predicateList) {
                try {
                    CustomDepPredicate predicate = Class.forName(predicateType.getClassName()).asSubclass(CustomDepPredicate.class).getDeclaredConstructor().newInstance();
                    if (!predicate.test(targetClassNode)) {
                        return false;
                    }
                } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                    ModInfo.LOGGER.warn("fuckyou");
                    throw new IllegalStateException("get CustomDepPredicate fail!");
                }
            }
        }
        return true;
    }

    public boolean checkDependencies(ClassNode mixinClassNode, String targetClassName) {
        AnnotationNode dependencies = Annotations.getInvisible(mixinClassNode, Dependencies.class);
        if (Annotations.getInvisible(mixinClassNode, Dependencies.class) != null) {
            List<AnnotationNode> dependencyArray = Annotations.getValue(dependencies, "dependencyList");
            for (AnnotationNode dependency : dependencyArray) {
                if (!checkDependency(targetClassName, dependency)) {
                    return false;
                }
            }
        }
        return true;
    }
}

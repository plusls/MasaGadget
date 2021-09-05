package com.plusls.MasaGadget;

import com.plusls.MasaGadget.mixin.CustomDepPredicate;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.NeedObfuscate;
import com.plusls.MasaGadget.util.YarnUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.util.version.VersionPredicateParser;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MasaGadgetMixinPlugin implements IMixinConfigPlugin {

    public static final String TWEAKEROO_MOD_ID = "tweakeroo";
    public static final String MINIHUD_MOD_ID = "minihud";
    public static final String LITEMATICA_MOD_ID = "litematica";
    public static final String BBOR_MOD_ID = "bbor";

    public static boolean isTweakerooLoaded = false;
    public static boolean isMinihudLoaded = false;
    public static boolean isLitematicaLoaded = false;
    public static boolean isBborLoaded = false;

    private final List<String> obfuscatedMixinList = new ArrayList<>();
    static private Path tempDirectory;

    static {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            try {
                tempDirectory = createTempDirectory();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Cannot create temp directory.");
            }
        }
    }


    @Override
    public void onLoad(String mixinPackage) {
        isTweakerooLoaded = FabricLoader.getInstance().isModLoaded(TWEAKEROO_MOD_ID);
        isMinihudLoaded = FabricLoader.getInstance().isModLoaded(MINIHUD_MOD_ID);
        isLitematicaLoaded = FabricLoader.getInstance().isModLoaded(LITEMATICA_MOD_ID);
        isBborLoaded = FabricLoader.getInstance().isModLoaded(BBOR_MOD_ID);
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            Object urlLoader = Thread.currentThread().getContextClassLoader();
            Class<?> knotClassLoader;
            try {
                knotClassLoader = Class.forName("net.fabricmc.loader.launch.knot.KnotClassLoader");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new IllegalStateException("Cannot load class: net.fabricmc.loader.launch.knot.KnotClassLoader");
            }

            try {
                Method method = knotClassLoader.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(urlLoader, tempDirectory.toUri().toURL());
            } catch (MalformedURLException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
                throw new IllegalStateException("Cannot add custom class path to KnotClassLoader");
            }
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        ClassNode mixinClassNode = loadClassNode(mixinClassName);
        if (!checkDependencies(mixinClassNode, targetClassName)) {
            return false;
        }

        if (tempDirectory != null && Annotations.getInvisible(mixinClassNode, NeedObfuscate.class) != null) {
            obfuscateClass(mixinClassName);
            return false;
        }
        return true;
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


    private static ClassNode loadClassNode(String className) {
        ClassNode classNode;
        try {
            classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalStateException(String.format("load ClassNode: %s fail.", className));
        }
        return classNode;
    }

    public static Path createTempDirectory() throws IOException {
        final Path tmp = Files.createTempDirectory(String.format("%s-", ModInfo.MOD_ID));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileUtils.forceDelete(tmp.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        return tmp;
    }

    private static boolean getRemap(AnnotationNode annotationNode, boolean defaultValue) {
        Boolean ret = Annotations.getValue(annotationNode, "remap");
        if (ret == null) {
            return defaultValue;
        }
        return ret;
    }

    private final static List<String> NAME_LIST = Arrays.asList("method", "target");

    private static void obfuscateAnnotation(AnnotationNode annotationNode, boolean defaultRemap) {
        boolean remap = getRemap(annotationNode, defaultRemap);
        for (int i = 0; i < annotationNode.values.size(); i += 2) {
            if (annotationNode.values.get(i + 1) instanceof AnnotationNode) {
                AnnotationNode node = (AnnotationNode) annotationNode.values.get(i + 1);
                obfuscateAnnotation(node, remap);
            } else if (annotationNode.values.get(i + 1) instanceof ArrayList && ((ArrayList)annotationNode.values.get(i + 1)).size() > 0 && ((ArrayList)annotationNode.values.get(i + 1)).get(0) instanceof AnnotationNode) {
                ArrayList arrayList = (ArrayList) annotationNode.values.get(i + 1);
                ArrayList<AnnotationNode> subAnnotationNodeList = arrayList;
                for (AnnotationNode subAnnotationNode : subAnnotationNodeList) {
                    obfuscateAnnotation(subAnnotationNode, remap);
                }
            } else if (!defaultRemap) {
                String name = (String) annotationNode.values.get(i);
                if (NAME_LIST.contains(name)) {
                    if (annotationNode.values.get(i + 1) instanceof String) {
                        String s = (String) annotationNode.values.get(i + 1);
                        annotationNode.values.set(i + 1, YarnUtil.obfuscateString(s));
                    } else if (annotationNode.values.get(i + 1) instanceof ArrayList) {
                        ArrayList arrayList = (ArrayList) annotationNode.values.get(i + 1);
                        if (arrayList.size() > 0 && arrayList.get(0) instanceof String) {
                            ArrayList<String> strList = arrayList;
                            for (int j = 0; j < strList.size(); ++j) {
                                strList.set(j, YarnUtil.obfuscateString(strList.get(j)));
                            }
                        }
                    }
                }
            }
        }
    }

    private static void obfuscateAnnotation(ClassNode classNode, Path outputDirectory) throws IOException {
        String fullClassName = classNode.name;
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        boolean classRemap = getRemap(Annotations.getInvisible(classNode, Mixin.class), true);

        for (MethodNode method : classNode.methods) {
            if (method.visibleAnnotations != null) {
                for (AnnotationNode annotationNode : method.visibleAnnotations) {
                    obfuscateAnnotation(annotationNode, classRemap);
                }
            }
        }
        classNode.accept(classWriter);

        int packageNameIdx = fullClassName.lastIndexOf('/');
        String packageName, className;
        if (packageNameIdx == -1) {
            packageName = "";
            className = fullClassName;
        } else {
            packageName = fullClassName.substring(0, packageNameIdx);
            className = fullClassName.substring(packageNameIdx + 1);
        }

        classNode.invisibleAnnotations.remove(Annotations.getInvisible(classNode, NeedObfuscate.class));
        Files.createDirectories(Paths.get(outputDirectory.toString(), packageName));
        Files.write(Paths.get(outputDirectory.toString(), packageName, className + "Obfuscated.class"), classWriter.toByteArray());
    }

    private void obfuscateClass(String classFullName) {
        ClassNode classNode = loadClassNode(classFullName);
        AnnotationNode needObfuscate = Annotations.getInvisible(classNode, NeedObfuscate.class);
        if (needObfuscate != null) {
            try {
                obfuscateAnnotation(classNode, tempDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("obfuscateAnnotation fail!");
            }
            String packageName = Annotations.getValue(needObfuscate, "packageName");
            obfuscatedMixinList.add(String.format("%sObfuscated", classFullName.replace(packageName + ".", "")));
        }
    }

    public static boolean checkDependency(String modId, String version) {
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(modId);
        if (modContainerOptional.isPresent()) {
            ModContainer modContainer = modContainerOptional.get();
            // not work in fabric-loader 0.12
            try {
                return VersionPredicateParser.matches(modContainer.getMetadata().getVersion(), version);
            } catch (VersionParsingException e) {
                e.printStackTrace();
                throw new IllegalStateException(String.format("VersionParsingException, modid=%s, version=%s", modId, version));
            }
        }
        return false;
    }


    private boolean checkDependency(String targetClassName, AnnotationNode dependency) {
        String modId = Annotations.getValue(dependency, "modId");
        List<String> versionList = Annotations.getValue(dependency, "version");

        for (String version : versionList) {
            // not work in fabric-loader 0.12
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

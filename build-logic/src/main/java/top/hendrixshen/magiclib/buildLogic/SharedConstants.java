package top.hendrixshen.magiclib.buildLogic;

public class SharedConstants {
    public static final String PLATFORM_PROPERTY = "loom.platform";

    public static class Catalogs {
        public static final String LIBS = "libs";
        public static final String FORGES = "forges";
    }

    public static class MagicProperties {
    }

    public static class ConfigurationNames {
        public static final String AUTO_API = "autoApi";
        public static final String AUTO_COMPILE_ONLY = "autoCompileOnly";
        public static final String AUTO_COMPILE_ONLY_API = "autoCompileOnlyApi";
        public static final String AUTO_RUNTIME_ONLY = "autoRuntimeOnly";
        public static final String AUTO_LOCAL_RUNTIME = "autoLocalRuntime";
        public static final String AUTO_IMPLEMENTATION = "autoImplementation";
        // Loom Remap Configurations
        public static final String MOD_API = "modApi";
        public static final String MOD_COMPILE_ONLY = "modCompileOnly";
        public static final String MOD_COMPILE_ONLY_API = "modCompileOnlyApi";
        public static final String MOD_RUNTIME_ONLY = "modRuntimeOnly";
        public static final String MOD_LOCAL_RUNTIME = "modLocalRuntime";
        public static final String MOD_IMPLEMENTATION = "modImplementation";
    }

    public static class CommonDependencies {
        public static final String MINECRAFT_GROUP = "com.mojang";
        public static final String MINECRAFT_NAME = "minecraft";
        public static final String FORGE_GROUP = "net.minecraftforge";
        public static final String FORGE_NAME = "forge";
        public static final String NEO_FORGE_GROUP = "net.neoforged";
        public static final String NEO_FORGE_NAME = "neoforge";
    }
}

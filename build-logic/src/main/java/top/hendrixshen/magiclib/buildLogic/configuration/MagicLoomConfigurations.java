package top.hendrixshen.magiclib.buildLogic.configuration;

import net.fabricmc.loom.LoomRemapGradlePlugin;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier;
import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import net.fabricmc.loom.LoomGradlePlugin;
import net.fabricmc.loom.LoomNoRemapGradlePlugin;
import net.fabricmc.loom.api.LoomGradleExtensionAPI;
import net.fabricmc.loom.configuration.ide.RunConfigSettings;
import net.fabricmc.loom.task.LoomTasks;
import net.fabricmc.loom.task.RunGameTask;
import net.fabricmc.loom.util.Constants.Configurations;
import net.fabricmc.loom.util.ModPlatform;

import top.hendrixshen.magiclib.buildLogic.MagicLoomExtension;
import top.hendrixshen.magiclib.buildLogic.SharedConstants.Catalogs;
import top.hendrixshen.magiclib.buildLogic.SharedConstants.CommonDependencies;
import top.hendrixshen.magiclib.buildLogic.SharedConstants.ConfigurationNames;
import top.hendrixshen.magiclib.buildLogic.util.ProjectDetail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import javax.inject.Inject;

public abstract class MagicLoomConfigurations implements Runnable {
    protected ProjectDetail projectDetail;

    private static DefaultMinimalDependency createDependency(String group, String name, String version) {
        return new DefaultMinimalDependency(DefaultModuleIdentifier.newId(group, name), new DefaultMutableVersionConstraint(version));
    }

    @Inject
    protected abstract Project getProject();

    @Inject
    protected abstract ConfigurationContainer getConfigurations();

    @Inject
    protected abstract DependencyHandler getDependencies();

    @Inject
    protected abstract TaskContainer getTasks();

    @Override
    public void run() {
        MagicLoomExtension magicLoomExtension = MagicLoomExtension.get(this.getProject());
        this.projectDetail = magicLoomExtension.getProjectDetail().get();

        if (this.projectDetail.isUnobfuscatedVersion()) {
            this.getProject().getPlugins().apply(LoomNoRemapGradlePlugin.class);
        } else {
            this.getProject().getPlugins().apply(LoomRemapGradlePlugin.class);
        }

        this.register(ConfigurationNames.AUTO_API, Role.RESOLVABLE);
        this.register(ConfigurationNames.AUTO_COMPILE_ONLY, Role.RESOLVABLE);
        this.register(ConfigurationNames.AUTO_COMPILE_ONLY_API, Role.RESOLVABLE);
        this.register(ConfigurationNames.AUTO_RUNTIME_ONLY, Role.RESOLVABLE);
        this.register(ConfigurationNames.AUTO_LOCAL_RUNTIME, Role.RESOLVABLE);
        this.register(ConfigurationNames.AUTO_IMPLEMENTATION, Role.RESOLVABLE);

        if (this.projectDetail.isUnobfuscatedVersion()) {
            this.extendsFrom(JavaPlugin.API_CONFIGURATION_NAME, ConfigurationNames.AUTO_API);
            this.extendsFrom(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, ConfigurationNames.AUTO_COMPILE_ONLY);
            this.extendsFrom(JavaPlugin.COMPILE_ONLY_API_CONFIGURATION_NAME, ConfigurationNames.AUTO_COMPILE_ONLY_API);
            this.extendsFrom(JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME, ConfigurationNames.AUTO_RUNTIME_ONLY);
            this.extendsFrom(Configurations.LOCAL_RUNTIME, ConfigurationNames.AUTO_LOCAL_RUNTIME);
            this.extendsFrom(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, ConfigurationNames.AUTO_IMPLEMENTATION);
        } else {
            this.extendsFrom(ConfigurationNames.MOD_API, ConfigurationNames.AUTO_API);
            this.extendsFrom(ConfigurationNames.MOD_COMPILE_ONLY, ConfigurationNames.AUTO_COMPILE_ONLY);
            this.extendsFrom(ConfigurationNames.MOD_COMPILE_ONLY_API, ConfigurationNames.AUTO_COMPILE_ONLY_API);
            this.extendsFrom(ConfigurationNames.MOD_RUNTIME_ONLY, ConfigurationNames.AUTO_RUNTIME_ONLY);
            this.extendsFrom(ConfigurationNames.MOD_LOCAL_RUNTIME, ConfigurationNames.AUTO_LOCAL_RUNTIME);
            this.extendsFrom(ConfigurationNames.MOD_IMPLEMENTATION, ConfigurationNames.AUTO_IMPLEMENTATION);
        }

        this.getProject().getPlugins().withType(LoomGradlePlugin.class).configureEach(this::configureLoom);
    }

    private void configureLoom(LoomGradlePlugin loomGradlePlugin) {
        LoomGradleExtensionAPI loom = this.getProject().getExtensions().getByType(LoomGradleExtensionAPI.class);
        this.setupLoomExtension(loom);
        this.setupMinecraftDependency(loom);
        this.setupModLoaderDependency();
    }

    @SuppressWarnings({"UnstableApiUsage", "CodeBlock2Expr"})
    private void setupLoomExtension(LoomGradleExtensionAPI loom) {
        loom.silentMojangMappingsLicense();
        loom.getRunConfigs().configureEach(runConfig -> {
            runConfig.property("mixin.debug.export", "true");
        });

        if (loom.getPlatform().get() == ModPlatform.FABRIC || loom.getPlatform().get() == ModPlatform.QUILT) {
            loom.mixin(mixin -> {
                mixin.getUseLegacyMixinAp().set(!this.projectDetail.isUnobfuscatedVersion());
            });
        }

        if (loom.getPlatform().get() == ModPlatform.FORGE) {
            loom.forge(forge -> {
                forge.getConvertAccessWideners().set(true);
                forge.mixinConfig(this.getProject().getParent().property("mod.id") + ".mixins.json");
            });

        }

        // Setup client default settings.
        RunConfigSettings client = loom.getRunConfigs().getByName("client");
        // client.programArg();
        client.vmArg("-Dmagiclib.debug=true");
        client.vmArg("-Dmagiclib.dev.qol=true");
        client.vmArg("-Dmagiclib.dev.qol.dfu.destroy=true");
        client.setRunDir("run/client");
        TaskProvider<RunGameTask> runClientTask = this.getTasks().named(LoomTasks.getRunConfigTaskName(client), RunGameTask.class);
        runClientTask.configure(task -> {
            task.setDefaultCharacterEncoding("UTF-8");
            task.getInputs().property("runDir", this.getProject().file(client.getRunDir()).toPath());

            task.doFirst(t -> {
                Path runDir = (Path) task.getInputs().getProperties().get("runDir");
                boolean _b = runDir.resolve("configs").toFile().mkdirs();
                File options = runDir.resolve("options.txt").toFile();

                if (options.exists()) {
                    return;
                }

                _b = options.getParentFile().mkdirs();

                try (PrintWriter writer = new PrintWriter(options)) {
                    writer.println("autoJump:false");
                    writer.println("enableVsync:false");
                    writer.println("forceUnicodeFont:true");
                    writer.println("fov:1.0");

                    if (this.projectDetail.getMinecraftVersionNumber() < 1_19_00) {
                        writer.println("gamma:16.0");
                    }

                    writer.println("guiScale:3");
                    writer.println("lang:" + Locale.getDefault().toString().toLowerCase());
                    writer.println("maxFps:260");
                    writer.println("renderDistance:10");
                    writer.println("soundCategory_master:0.0");
                } catch (IOException e) {
                    // ignore
                }
            });
        });

        // Setup server default settings.
        RunConfigSettings server = loom.getRunConfigs().getByName("server");
        server.vmArg("-Dmagiclib.debug=true");
        server.vmArg("-Dmagiclib.dev.qol=true");
        server.vmArg("-Dmagiclib.dev.qol.dfu.destroy=true");
        server.setRunDir("run/server");
        TaskProvider<RunGameTask> runServerTask = this.getTasks().named(LoomTasks.getRunConfigTaskName(server), RunGameTask.class);

        // Agree eula before server init.
        runServerTask.configure(task -> {
            task.setDefaultCharacterEncoding("UTF-8");
            task.getInputs().property("runDir", this.getProject().file(server.getRunDir()).toPath());

            task.doFirst(t -> {
                Path runDir = (Path) task.getInputs().getProperties().get("runDir");
                File eula = runDir.resolve("eula.txt").toFile();

                if (eula.exists()) {
                    return;
                }

                boolean _b = eula.getParentFile().mkdirs();

                try (PrintWriter writer = new PrintWriter(eula)) {
                    writer.println("#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
                    writer.println("#" + new Date());
                    writer.println("eula=true");
                } catch (IOException e) {
                    // ignore
                }
            });
        });
    }

    private void setupMinecraftDependency(LoomGradleExtensionAPI loom) {
        this.getDependencies().add(Configurations.MINECRAFT, createDependency(CommonDependencies.MINECRAFT_GROUP, CommonDependencies.MINECRAFT_NAME, this.projectDetail.getMinecraftVersionName()));

        if (!this.projectDetail.isUnobfuscatedVersion()) {
            this.getDependencies().add(Configurations.MAPPINGS, loom.officialMojangMappings());
        }
    }

    private void setupModLoaderDependency() {
        VersionCatalogsExtension catalogsExtension = this.getProject().getExtensions().getByType(VersionCatalogsExtension.class);
        ModPlatform modPlatform = Optional.ofNullable(this.projectDetail.getPlatform()).orElse(ModPlatform.FABRIC);

        if (modPlatform == ModPlatform.FABRIC) {
            VersionCatalog libs = catalogsExtension.named(Catalogs.LIBS);
            // TODO: Dedicate exception
            this.getDependencies().add(ConfigurationNames.AUTO_API, libs.findLibrary("fabric-loader").orElseThrow(RuntimeException::new));
            return;
        }

        VersionCatalog forges = catalogsExtension.named(Catalogs.FORGES);

        switch (modPlatform) {
            case FORGE:
                this.getDependencies().add(Configurations.FORGE,
                        MagicLoomConfigurations.createDependency(CommonDependencies.FORGE_GROUP, CommonDependencies.FORGE_NAME,
                                forges.findVersion("forge-mc" + this.projectDetail.getMinecraftVersionNumber())
                                        .orElseThrow(ForgeDependencyException::new).getDisplayName()));
                break;
            case NEOFORGE:
                this.getDependencies().add(Configurations.NEOFORGE,
                        MagicLoomConfigurations.createDependency(CommonDependencies.NEO_FORGE_GROUP, CommonDependencies.NEO_FORGE_NAME,
                                forges.findVersion("neoforge-mc" + this.projectDetail.getMinecraftVersionNumber())
                                        .orElseThrow(ForgeDependencyException::new).getDisplayName()));
                break;
        }
    }

    private NamedDomainObjectProvider<Configuration> register(String name, Role role) {
        return this.getConfigurations().register(name, role::apply);
    }

    private NamedDomainObjectProvider<Configuration> registerNonTransitive(String name, Role role) {
        final NamedDomainObjectProvider<Configuration> provider = register(name, role);
        provider.configure(configuration -> configuration.setTransitive(false));
        return provider;
    }

    public void extendsFrom(String a, String b) {
        this.getConfigurations().getByName(a, configuration -> configuration.extendsFrom(this.getConfigurations().getByName(b)));
    }

    public enum Role {
        NONE(false, false),
        CONSUMABLE(true, false),
        RESOLVABLE(false, true);

        private final boolean canBeConsumed;
        private final boolean canBeResolved;

        Role(boolean canBeConsumed, boolean canBeResolved) {
            this.canBeConsumed = canBeConsumed;
            this.canBeResolved = canBeResolved;
        }

        public void apply(Configuration configuration) {
            configuration.setCanBeConsumed(this.canBeConsumed);
            configuration.setCanBeResolved(this.canBeResolved);
        }
    }

    private final class ForgeDependencyException extends RuntimeException {
        public ForgeDependencyException() {
            super("Couldn't locate " + MagicLoomConfigurations.this.projectDetail.getPlatform().id() + " version for " + MagicLoomConfigurations.this.getProject().getName() + " in version catalog");
        }
    }
}

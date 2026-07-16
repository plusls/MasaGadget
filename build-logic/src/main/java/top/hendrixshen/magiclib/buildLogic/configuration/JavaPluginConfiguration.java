package top.hendrixshen.magiclib.buildLogic.configuration;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;

import top.hendrixshen.magiclib.buildLogic.MagicLoomExtension;
import top.hendrixshen.magiclib.buildLogic.util.ProjectDetail;

import javax.inject.Inject;

public abstract class JavaPluginConfiguration implements Runnable {
    protected ProjectDetail projectDetail;

    @Inject
    protected abstract Project getProject();

    @Override
    public void run() {
        MagicLoomExtension magicLoomExtension = MagicLoomExtension.get(this.getProject());
        this.projectDetail = magicLoomExtension.getProjectDetail().get();
        this.getProject().getPlugins().apply(JavaLibraryPlugin.class);
        this.getProject().getPlugins().withType(JavaPlugin.class).configureEach(this::configureJava);
    }

    private void configureJava(JavaPlugin javaPlugin) {
        this.configureJavaExtension();
        this.getProject().getTasks().withType(JavaCompile.class).configureEach(this::configureJavaCompileTask);
    }

    private void configureJavaExtension() {
        JavaPluginExtension java = this.getProject().getExtensions().getByType(JavaPluginExtension.class);
        int mcVersion = this.projectDetail.getMinecraftVersionNumber();

        if (mcVersion >= 26_01_00) {
            java.setSourceCompatibility(JavaVersion.VERSION_25);
            java.setTargetCompatibility(JavaVersion.VERSION_25);
        } else if (mcVersion >= 1_20_05) {
            java.setSourceCompatibility(JavaVersion.VERSION_21);
            java.setTargetCompatibility(JavaVersion.VERSION_21);
        } else if (mcVersion >= 1_18_00) {
            java.setSourceCompatibility(JavaVersion.VERSION_17);
            java.setTargetCompatibility(JavaVersion.VERSION_17);
        } else if (mcVersion >= 1_17_00) {
            java.setSourceCompatibility(JavaVersion.VERSION_16);
            java.setTargetCompatibility(JavaVersion.VERSION_16);
        } else {
            java.setSourceCompatibility(JavaVersion.VERSION_1_8);
            java.setTargetCompatibility(JavaVersion.VERSION_1_8);
        }

        java.withSourcesJar();
        java.withJavadocJar();
    }

    private void configureJavaCompileTask(JavaCompile task) {
        task.getOptions().setEncoding("UTF-8");
        int mcVersion = this.projectDetail.getMinecraftVersionNumber();

        if (mcVersion >= 26_01_00) {
            task.getOptions().getRelease().set(25);
        } else if (mcVersion >= 1_20_05) {
            task.getOptions().getRelease().set(21);
        } else if (mcVersion >= 1_18_00) {
            task.getOptions().getRelease().set(17);
        } else if (mcVersion >= 1_17_00) {
            task.getOptions().getRelease().set(16);
        } else {
            task.getOptions().getRelease().set(8);
            // suppressed "source/target value 8 is obsolete and will be removed in a future release"
            task.getOptions().getCompilerArgs().add("-Xlint:-options");
        }

        task.getOptions().getCompilerArgs().add("-Xlint:deprecation");
        task.getOptions().getCompilerArgs().add("-Xlint:unchecked");
    }
}

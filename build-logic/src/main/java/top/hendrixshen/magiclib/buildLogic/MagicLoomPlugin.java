package top.hendrixshen.magiclib.buildLogic;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import top.hendrixshen.magiclib.buildLogic.configuration.JavaPluginConfiguration;
import top.hendrixshen.magiclib.buildLogic.configuration.MagicLoomConfigurations;
import top.hendrixshen.magiclib.buildLogic.configuration.Preprocessor;

import java.util.List;

public class MagicLoomPlugin implements Plugin<Project> {
    public static final String NAME = "top.hendrixshen.magiclib.build-logic.magic-loom";

    private static final List<Class<? extends Runnable>> SETUP_JOBS = List.of(
            MagicLoomConfigurations.class,
            JavaPluginConfiguration.class,
            Preprocessor.class
    );

    @Override
    public void apply(Project project) {
        Project parentProject = project.getParent();

        if (parentProject == null) {
            throw new GradleException(MagicLoomPlugin.NAME + "couldn't apply on root project!");
        }

        if (!parentProject.getPlugins().hasPlugin(RootMagicLoomPlugin.NAME)) {
            throw new GradleException("Apply" + RootMagicLoomPlugin.NAME + " on parent project first!");
        }

        project.getExtensions().create(MagicLoomExtension.class, "magic", MagicLoomExtensionImpl.class, project);

        for (Class<? extends Runnable> jobClass : SETUP_JOBS) {
            project.getObjects().newInstance(jobClass).run();
        }
    }
}

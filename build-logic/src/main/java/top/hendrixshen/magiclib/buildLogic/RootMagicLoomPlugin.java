package top.hendrixshen.magiclib.buildLogic;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import top.hendrixshen.magiclib.buildLogic.configuration.root.Preprocessor;

public class RootMagicLoomPlugin implements Plugin<Project> {
    public static final String NAME = "top.hendrixshen.magiclib.build-logic.magic-loom-root";

    @Override
    public void apply(Project project) {
        project.getExtensions().create(RootMagicLoomExtension.class, "magic", RootMagicLoomExtensionImpl.class, project);
        project.getObjects().newInstance(Preprocessor.class).run();
    }
}

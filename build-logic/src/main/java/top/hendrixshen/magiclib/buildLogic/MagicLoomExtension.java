package top.hendrixshen.magiclib.buildLogic;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;

import top.hendrixshen.magiclib.buildLogic.util.ProjectDetail;

public interface MagicLoomExtension {
    static MagicLoomExtension get(Project project) {
        return (MagicLoomExtension) project.getExtensions().getByName("magic");
    }

    Property<ProjectDetail> getProjectDetail();
}

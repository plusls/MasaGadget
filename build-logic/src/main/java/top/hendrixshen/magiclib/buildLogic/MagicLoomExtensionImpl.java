package top.hendrixshen.magiclib.buildLogic;

import lombok.Getter;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

import org.gradle.api.provider.Property;

import top.hendrixshen.magiclib.buildLogic.util.ProjectDetail;

@Getter
public abstract class MagicLoomExtensionImpl implements MagicLoomExtension {
    private final Property<ProjectDetail> projectDetail;

    public MagicLoomExtensionImpl(Project project) {
        Project parentProject = project.getParent();
        assert parentProject != null;
        RootMagicLoomExtension rootMagicLoomExtension = parentProject.getExtensions().getByType(RootMagicLoomExtension.class);
        ProjectDetail detail = rootMagicLoomExtension.getProjectDetailMap().get().get(project.getName());

        if (detail == null) {
            throw new GradleException("Project " + project.getName() + " is not registered in root project " + parentProject.getName());
        }

        this.projectDetail = project.getObjects().property(ProjectDetail.class).convention(detail);
        this.projectDetail.finalizeValue();
    }
}

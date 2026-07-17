package top.hendrixshen.magiclib.buildLogic;

import org.gradle.api.Project;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

import top.hendrixshen.magiclib.buildLogic.RootMagicLoomExtensionImpl.ManualLinkDetail;
import top.hendrixshen.magiclib.buildLogic.util.ProjectDetail;

public interface RootMagicLoomExtension {
    static RootMagicLoomExtension get(Project project) {
        return (RootMagicLoomExtension) project.getExtensions().getByName("magic");
    }

    Property<Boolean> getMagiclibMode();

    Property<Boolean> getMultiPlatformSupport();

    Property<ExtraMappingFailureStrategy> getExtraMappingFailureStrategy();

    MapProperty<String, ManualLinkDetail> getManualLinkMap();

    MapProperty<String, ProjectDetail> getProjectDetailMap();

    void manualLink(String projectName, ProjectDetail to, String mappingFileName);

    void recordProjectDetail(String projectName, ProjectDetail detail);

    enum ExtraMappingFailureStrategy {
        IGNORE,
        FAIL,
        WARN
    }
}

package top.hendrixshen.magiclib.buildLogic;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.gradle.api.Project;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

import top.hendrixshen.magiclib.buildLogic.util.ProjectDetail;

@Getter
public abstract class RootMagicLoomExtensionImpl implements RootMagicLoomExtension {
    private final Property<Boolean> magiclibMode;
    private final Property<Boolean> multiPlatformSupport;
    private final Property<ExtraMappingFailureStrategy> extraMappingFailureStrategy;
    private final MapProperty<String, ManualLinkDetail> manualLinkMap;
    private final MapProperty<String, ProjectDetail> projectDetailMap;

    public RootMagicLoomExtensionImpl(Project project) {
        this.magiclibMode = project.getObjects().property(Boolean.class).convention(false);
        this.magiclibMode.finalizeValueOnRead();
        this.multiPlatformSupport = project.getObjects().property(Boolean.class).convention(false);
        this.multiPlatformSupport.finalizeValueOnRead();
        this.extraMappingFailureStrategy = project.getObjects().property(ExtraMappingFailureStrategy.class).convention(ExtraMappingFailureStrategy.FAIL);
        this.extraMappingFailureStrategy.finalizeValueOnRead();
        this.manualLinkMap = project.getObjects().mapProperty(String.class, ManualLinkDetail.class);
        this.projectDetailMap = project.getObjects().mapProperty(String.class, ProjectDetail.class);
    }

    @Override
    public void manualLink(String projectName, ProjectDetail to, String mappingFileName) {
        this.manualLinkMap.put(projectName, new ManualLinkDetail(to, mappingFileName));
    }

    @Override
    public void recordProjectDetail(String projectName, ProjectDetail detail) {
        this.projectDetailMap.put(projectName, detail);
    }

    @SuppressWarnings("ClassCanBeRecord")
    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ManualLinkDetail {
        private final ProjectDetail to;
        private final String mappingFileName;
    }
}

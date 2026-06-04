package top.hendrixshen.magiclib.buildLogic.configuration.root;

import com.replaymod.gradle.preprocess.Node;
import com.replaymod.gradle.preprocess.RootPreprocessExtension;
import com.replaymod.gradle.preprocess.RootPreprocessPlugin;
import groovy.json.JsonSlurper;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.fabricmc.loom.util.ModPlatform;

import top.hendrixshen.magiclib.buildLogic.RootMagicLoomExtension;
import top.hendrixshen.magiclib.buildLogic.RootMagicLoomExtension.ExtraMappingFailureStrategy;
import top.hendrixshen.magiclib.buildLogic.util.ProjectDetail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;

// TODO: Manual Link
public abstract class Preprocessor implements Runnable {
    @Inject
    protected abstract Project getProject();

    protected RootMagicLoomExtension magicLoomExtension;
    protected RootPreprocessExtension preprocessExtension;

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        this.getProject().getPlugins().apply(RootPreprocessPlugin.class);
        this.magicLoomExtension = this.getProject().getExtensions().getByType(RootMagicLoomExtension.class);
        this.preprocessExtension = this.getProject().getExtensions().getByType(RootPreprocessExtension.class);

        this.getProject().afterEvaluate(project -> {
            boolean magiclibMode = this.magicLoomExtension.getMagiclibMode().get();
            boolean multiPlatformSupport = this.magicLoomExtension.getMultiPlatformSupport().get();
            Map<String, ?> settings = this.getSettings();

            if (magiclibMode) {
                settings = (Map<String, ?>) settings.get("projects");

                if (settings == null) {
                    throw new GradleException("settings.json is not met MagicLib configuration");
                }

                settings = (Map<String, ?>) settings.get(project.getName());

                if (settings == null) {
                    throw new GradleException("Project " + project.getName() + " is not in settings.json");
                }
            }

            String prefix = (String) settings.get("prefix");
            this.preprocessExtension.getStrictExtraMappings().set(false);
            final ProjectDetail rootNodeDetail;

            if (magiclibMode) {
                if (prefix == null) {
                    throw new GradleException("prefix is not specified in settings.json:");
                }

                rootNodeDetail = ProjectDetail.createMagicLib(this.getRootNodeName().replaceAll(prefix + "-", ""), prefix);
            } else {
                rootNodeDetail = ProjectDetail.create(this.getRootNodeName());
            }

            this.magicLoomExtension.recordProjectDetail(rootNodeDetail.getProjectNameReal(), rootNodeDetail);
            List<ProjectDetail> details = ((List<String>) settings.get("versions")).stream().map(p -> {
                if (magiclibMode) {
                    return ProjectDetail.createMagicLib(p, prefix);
                } else {
                    return ProjectDetail.create(p);
                }
            }).collect(Collectors.toList());

            int rootIdx = details.indexOf(rootNodeDetail);
            Node rootNode = this.preprocessExtension.createNode(this.getRootNodeName(), rootNodeDetail.getMinecraftVersionNumber(), "");
            this.buildVersionChain(details, rootIdx, rootNode, multiPlatformSupport);
        });
    }

    private void buildVersionChain(List<ProjectDetail> details, int rootIdx, Node rootNode, boolean multiPlatformSupport) {
        ProjectDetail rootNodeDetail = details.get(rootIdx);

        if (multiPlatformSupport) {
            this.linkPlatformBranches(rootNode, rootNodeDetail, details);
        }

        this.processVersionRange(details, rootIdx - 1, 0, -1,
                rootNode, rootNodeDetail, multiPlatformSupport);
        this.processVersionRange(details, rootIdx + 1, details.size(), 1,
                rootNode, rootNodeDetail, multiPlatformSupport);
    }

    @SuppressWarnings({"SimplifiableConditionalExpression"})
    private void processVersionRange(List<ProjectDetail> details, int start, int end, int step,
                                     Node initialPreviousNode, ProjectDetail rootNodeDetail,
                                     boolean multiPlatformSupport) {
        Node previousNode = initialPreviousNode;

        // noinspection Pointless
        for (int i = start; (step < 0) ? (i >= end) : (i < end); i += step) {
            ProjectDetail detail = details.get(i);

            if (!Objects.equals(rootNodeDetail.getPlatform(), detail.getPlatform())) {
                continue;
            }

            int nextIdx = step < 0 ? i + 1 : i - 1;
            Node node = this.createNode(detail);
            String mappingFilePath = this.buildMappingFileName(
                    details.get(nextIdx),
                    detail,
                    multiPlatformSupport
            );

            File mappingFile = this.checkMappingFile(this.getProject().file(mappingFilePath));
            previousNode.link(node, mappingFile);
            this.magicLoomExtension.recordProjectDetail(detail.getProjectNameReal(), detail);
            // this.getProject().getLogger().lifecycle("Linked {} to {} with {}", previousNode.getProject(), node.getProject(), mappingFile);

            if (multiPlatformSupport) {
                this.linkPlatformBranches(node, detail, details);
            }

            previousNode = node;
        }
    }

    private void linkPlatformBranches(Node mainNode, ProjectDetail detail, List<ProjectDetail> details) {
        ModPlatform platform = detail.getPlatform();

        if (platform != ModPlatform.FABRIC) {
            throw new GradleException("Unsupported platform " + platform + " on mainchain.");
        }

        this.tryLinkPlatform(mainNode, detail, details, ModPlatform.QUILT);
        Node forgeNode = this.tryLinkPlatform(mainNode, detail, details, ModPlatform.FORGE);

        if (forgeNode == null) {
            this.tryLinkPlatform(mainNode, detail, details, ModPlatform.NEOFORGE);
        } else {
            this.tryLinkPlatform(forgeNode, detail, details, ModPlatform.NEOFORGE);
        }
    }

    private @Nullable Node tryLinkPlatform(Node sourceNode, ProjectDetail sourceDetail, List<ProjectDetail> details, ModPlatform platform) {
        ProjectDetail targetPlatformDetail = ProjectDetail.createOtherPlatform(sourceDetail, platform);

        if (details.contains(targetPlatformDetail)) {
            File mappingFile = this.checkMappingFile(this.getProject().file(String.format("versions/mapping-%s-%s-%s.txt", sourceDetail.getMinecraftVersionName(), sourceDetail.getPlatform().id(), targetPlatformDetail.getPlatform().id())));
            Node targetNode = this.createNode(targetPlatformDetail);
            sourceNode.link(targetNode, mappingFile);
            this.magicLoomExtension.recordProjectDetail(targetPlatformDetail.getProjectNameReal(), targetPlatformDetail);
            // this.getProject().getLogger().lifecycle("Linked {} to {}", sourceNode.getProject(), targetNode.getProject());
            return targetNode;
        }

        return null;
    }

    private Node createNode(ProjectDetail detail) {
        return new Node(detail.getProjectNameReal(), detail.getMinecraftVersionNumber(), "");
    }

    private String buildMappingFileName(ProjectDetail sourceDetail, ProjectDetail targetDetail, boolean multiPlatformSupport) {
        StringBuilder mapping = new StringBuilder("versions/mapping-");

        if (multiPlatformSupport) {
            mapping.append(sourceDetail.getPlatform().id()).append("-");
        }

        mapping.append(sourceDetail.getMinecraftVersionName())
                .append("-")
                .append(targetDetail.getMinecraftVersionName())
                .append(".txt");

        return mapping.toString();
    }

    private @Nullable File checkMappingFile(@NonNull File mappingFile) {
        if (!mappingFile.exists()) {
            if (this.magicLoomExtension.getExtraMappingFailureStrategy().get() == ExtraMappingFailureStrategy.FAIL) {
                throw new GradleException("Mapping file " + mappingFile + " does not exist.");
            }

            if (this.magicLoomExtension.getExtraMappingFailureStrategy().get() == ExtraMappingFailureStrategy.WARN) {
                this.getProject().getLogger().warn("Mapping file {} does not exist.", mappingFile);
            }

            mappingFile = null;
        }

        return mappingFile;
    }

    private Map<String, ?> getSettings() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, ?> settings = (Map<String, ?>) new JsonSlurper().parse(this.getProject().getRootDir().toPath().resolve("settings.json"));
            return settings;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getRootNodeName() {
        File file = this.getProject().file("./versions/mainProject");

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        throw new GradleException("mainProject file not found");
    }
}

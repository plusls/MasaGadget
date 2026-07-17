package top.hendrixshen.magiclib.buildLogic.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.gradle.api.Project;
import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.loom.util.ModPlatform;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@ToString
@EqualsAndHashCode
public class ProjectDetail {
    private final static List<String> BLACKLIST_FLAGS = ImmutableList.of("MC", "FABRIC", "QUILT", "FORGE", "NEOFORGE",
            "FABRIC_LIKE", "FORGE_LIKE");
    private static final Logger LOGGER = LogManager.getLogger(ProjectDetail.class);

    private final String projectName;
    private final String prefix;
    private final ModPlatform platform;
    private final String minecraftVersionName;
    private final boolean magiclibMode;
    private final int minecraftMajor;
    private final int minecraftMinor;
    private final int minecraftPatch;
    private final List<String> extraFlags = Lists.newArrayList();

    public static ProjectDetail createOtherPlatform(ProjectDetail detail, ModPlatform modPlatform) {
        if (detail.getPlatform() == modPlatform) {
            return detail;
        }

        return new ProjectDetail(detail.projectName.replaceAll(detail.platform.id(), modPlatform.id()), detail.prefix, modPlatform,
                detail.minecraftVersionName, detail.magiclibMode, detail.extraFlags);
    }

    public static ProjectDetail create(String project) {
        return new ProjectDetail(project, null, false);
    }

    @ApiStatus.Internal
    public static ProjectDetail createMagicLib(String project, String prefix) {
        return new ProjectDetail(project, prefix, true);
    }

    public static ProjectDetail create(String project, ModPlatform modPlatform, String minecraftVersionName, List<String> extraFlags) {
        return new ProjectDetail(project, null, modPlatform, minecraftVersionName, false, extraFlags);
    }

    @ApiStatus.Internal
    public static ProjectDetail createMagicLib(String project, String prefix, ModPlatform modPlatform, String minecraftVersionName, List<String> extraFlags) {
        return new ProjectDetail(project, prefix, modPlatform, minecraftVersionName, true, extraFlags);
    }

    private ProjectDetail(String project, String prefix, ModPlatform modPlatform, String minecraftVersionName, boolean magiclibMode, List<String> extraFlags) {
        Matcher matcher = Pattern.compile("^(?<version>(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)(?:\\.(?<patch>0|[1-9]\\d*))?)$").matcher(minecraftVersionName);

        if (!matcher.find()) {
            throw new ProjectParseException("Invalid minecraft version name: " + project);
        }

        this.projectName = project;
        this.prefix = prefix;
        this.platform = modPlatform;
        this.minecraftVersionName = minecraftVersionName;
        this.magiclibMode = magiclibMode;
        this.minecraftMajor = Integer.parseInt(matcher.group("major"));
        this.minecraftMinor = Integer.parseInt(matcher.group("minor"));
        this.minecraftPatch = Optional.ofNullable(matcher.group("patch")).map(Integer::parseInt).orElse(0);
        extraFlags.stream().filter(flag -> {
            if (ProjectDetail.BLACKLIST_FLAGS.contains(flag)) {
                ProjectDetail.LOGGER.warn("Ignored blacklist flag: {}" + flag);
                return false;
            }

            return true;
        }).forEach(this.extraFlags::add);
    }

    private ProjectDetail(String project, String prefix, boolean magiclibMode) {
        Matcher matcher = Pattern.compile("^(?<version>(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)(?:\\.(?<patch>0|[1-9]\\d*))?)(?:-(?<platform>fabric|quilt|forge|neoforge))?$").matcher(project);

        if (!matcher.find()) {
            throw new ProjectParseException("Invalid project name: " + project);
        }

        this.projectName = project;
        this.prefix = prefix;
        this.platform = ModPlatform.valueOf(matcher.group("platform") == null ? "FABRIC" : matcher.group("platform").toUpperCase());
        this.minecraftVersionName = matcher.group("version");
        this.magiclibMode = magiclibMode;
        this.minecraftMajor = Integer.parseInt(matcher.group("major"));
        this.minecraftMinor = Integer.parseInt(matcher.group("minor"));
        this.minecraftPatch = Optional.ofNullable(matcher.group("patch")).map(Integer::parseInt).orElse(0);
    }

    public String getProjectNameReal() {
        if (this.magiclibMode) {
            return this.prefix + "-" + this.projectName;
        }

        return this.projectName;
    }

    public boolean isUnobfuscatedVersion() {
        return this.getMinecraftVersionNumber() >= 26_00_00;
    }

    public int getMinecraftVersionNumber() {
        return this.minecraftMajor * 1_00_00 + this.minecraftMinor * 1_00 + this.minecraftPatch;
    }

    public static class ProjectParseException extends IllegalArgumentException {
        public ProjectParseException(String message) {
            super(message);
        }
    }
}

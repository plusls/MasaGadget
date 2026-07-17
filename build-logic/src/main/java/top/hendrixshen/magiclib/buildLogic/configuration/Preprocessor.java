package top.hendrixshen.magiclib.buildLogic.configuration;

import com.replaymod.gradle.preprocess.PreprocessExtension;
import com.replaymod.gradle.preprocess.PreprocessPlugin;
import org.gradle.api.Project;

import net.fabricmc.loom.util.ModPlatform;

import top.hendrixshen.magiclib.buildLogic.MagicLoomExtension;
import top.hendrixshen.magiclib.buildLogic.util.ProjectDetail;

import javax.inject.Inject;

public abstract class Preprocessor implements Runnable {
    protected ProjectDetail projectDetail;

    @Inject
    protected abstract Project getProject();

    @Override
    public void run() {
        this.getProject().getPlugins().apply(PreprocessPlugin.class);
        MagicLoomExtension magicLoomExtension = MagicLoomExtension.get(this.getProject());
        this.projectDetail = magicLoomExtension.getProjectDetail().get();

        this.getProject().getPlugins().withType(PreprocessPlugin.class).configureEach(plugin -> {
            int mcVersion = this.projectDetail.getMinecraftVersionNumber();
            PreprocessExtension preprocessExtension = this.getProject().getExtensions().getByType(PreprocessExtension.class);
            preprocessExtension.getVars().put("MC", mcVersion);
            ModPlatform modPlatform = this.projectDetail.getPlatform();

            if (modPlatform != null) {
                preprocessExtension.getVars().put("FABRIC", modPlatform == ModPlatform.FABRIC ? 1 : 0);
                preprocessExtension.getVars().put("QUILT", modPlatform == ModPlatform.QUILT ? 1 : 0);
                preprocessExtension.getVars().put("FORGE", modPlatform == ModPlatform.FORGE ? 1 : 0);
                preprocessExtension.getVars().put("NEO_FORGE", modPlatform == ModPlatform.NEOFORGE ? 1 : 0);
                preprocessExtension.getVars().put("FABRIC_LIKE", !modPlatform.isForgeLike() ? 1 : 0);
                preprocessExtension.getVars().put("FORGE_LIKE", modPlatform.isForgeLike() ? 1 : 0);
            }
        });
    }
}

package top.hendrixshen.magiclib.buildLogic.config;

import org.gradle.api.Project;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier;
import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint;

import net.fabricmc.loom.util.ModPlatform;

import top.hendrixshen.magiclib.buildLogic.MagicLoomExtension;
import top.hendrixshen.magiclib.buildLogic.RootMagicLoomExtension;

import javax.inject.Inject;

public abstract class RootMagicLoomConfigurations implements Runnable {
    @Inject
    protected abstract Project getProject();


    @Override
    public void run() {
        final RootMagicLoomExtension extension = RootMagicLoomExtension.get(this.getProject());
    }
}

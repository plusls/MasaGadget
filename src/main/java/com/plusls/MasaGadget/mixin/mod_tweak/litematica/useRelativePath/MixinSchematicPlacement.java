package com.plusls.MasaGadget.mixin.mod_tweak.litematica.useRelativePath;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.dependency.DependencyType;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.platform.PlatformType;

import java.io.File;

@Dependencies(
        require = {
                @Dependency(value = ModId.malilib, versionPredicates = "<0.11.0"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FABRIC_LIKE)
        }
)
@Dependencies(
        require = {
                @Dependency(value = ModId.minecraft, versionPredicates = "<1.18-"),
                @Dependency(dependencyType = DependencyType.PLATFORM, platformType = PlatformType.FORGE_LIKE)
        }
)
@Mixin(value = SchematicPlacement.class, remap = false)
public class MixinSchematicPlacement {
    @Redirect(
            method = "toJson",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/io/File;getAbsolutePath()Ljava/lang/String;",
                    ordinal = 0
            )
    )
    private String toRelativePath(File file) {
        if (!Configs.useRelativePath.getBooleanValue()) {
            return file.getAbsolutePath();
        }

        if (file.isAbsolute()) {
            try {
                return MagicLib.getInstance().getCurrentPlatform().getGameFolder().relativize(file.toPath()).toString();
            } catch (IllegalArgumentException ignored) {
                return file.getAbsolutePath();
            }
        } else {
            return file.toString();
        }
    }
}

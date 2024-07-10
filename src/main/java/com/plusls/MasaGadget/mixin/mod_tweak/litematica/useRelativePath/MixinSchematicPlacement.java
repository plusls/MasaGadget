package com.plusls.MasaGadget.mixin.mod_tweak.litematica.useRelativePath;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

import java.io.File;

@Dependencies(require = @Dependency(ModId.litematica))
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
                return FabricLoader.getInstance().getGameDir().relativize(file.toPath()).toString();
            } catch (IllegalArgumentException ignored) {
                return file.getAbsolutePath();
            }
        } else {
            return file.toString();
        }
    }
}

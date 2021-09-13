package com.plusls.MasaGadget.mixin.litematica.useRelativePath;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;

@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.LITEMATICA_MOD_ID, version = "*"))
@Mixin(value = SchematicPlacement.class, remap = false)
public class MixinSchematicPlacement {
    @Redirect(method = "toJson", at = @At(value = "INVOKE", target = "Ljava/io/File;getAbsolutePath()Ljava/lang/String;", ordinal = 0))
    private String toRelativePath(File file) {
        if (!Configs.Litematica.USE_RELATIVE_PATH.getBooleanValue()) {
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

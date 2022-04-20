package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportFreeCamera;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

import java.util.UUID;

@Dependencies(and = {@Dependency(ModInfo.TWEAKEROO_MOD_ID), @Dependency(value = "minecraft", versionPredicate = "<=1.16.5")})
@Mixin(value = RenderUtils.class, remap = false)
public class MixinMixinRenderUtils {
    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getPlayerByUUID(Ljava/util/UUID;)Lnet/minecraft/world/entity/player/Player;",
                    ordinal = 0, remap = true))
    private static Player redirectGetPlayerByUuid(Level world, UUID uuid) {
        Player ret = world.getPlayerByUUID(uuid);
        if (Configs.inventoryPreviewSupportFreeCamera && FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue()) {
            ret = CameraEntity.getCamera();
        }
        return ret;
    }
}

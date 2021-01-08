package com.plusls.MasaGadget.mixin.tweakeroo.feature.inventoryPreviewSupportFreeCamera;

import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {
    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getPlayerByUuid(Ljava/util/UUID;)Lnet/minecraft/entity/player/PlayerEntity;",
                    ordinal = 0, remap = true))
    private static PlayerEntity redirectGetPlayerByUuid(World world, UUID uuid) {
        // support free camera
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue()) {
            return (PlayerEntity) MinecraftClient.getInstance().getCameraEntity();
        } else {
            return world.getPlayerByUuid(uuid);
        }
    }
}

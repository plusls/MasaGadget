package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportFreeCamera;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {
    @Redirect(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getPlayerByUuid(Ljava/util/UUID;)Lnet/minecraft/entity/player/PlayerEntity;",
                    ordinal = 0, remap = true))
    private static PlayerEntity redirectGetPlayerByUuid(World world, UUID uuid) {
        PlayerEntity ret = world.getPlayerByUuid(uuid);
        if (Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_FREE_CAMERA.getBooleanValue() && FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue()) {
            ret = (PlayerEntity) MinecraftClient.getInstance().getCameraEntity();
        }
        return ret;
    }
}

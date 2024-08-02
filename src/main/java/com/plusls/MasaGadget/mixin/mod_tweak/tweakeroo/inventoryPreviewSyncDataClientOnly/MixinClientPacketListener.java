package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSyncDataClientOnly;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
    @Inject(method = "handleOpenScreen", at = @At("RETURN"))
    private void postHandleOpenScreen(ClientboundOpenScreenPacket clientboundOpenScreenPacket, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!Configs.inventoryPreviewSyncDataClientOnly.getBooleanValue() ||
                (Configs.inventoryPreviewSyncData.getBooleanValue() && PcaSyncProtocol.enable) ||
                minecraft.hasSingleplayerServer() ||
                !FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() ||
                !Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld()
        ) {
            return;
        }

        minecraft.setScreen(null);
    }
}

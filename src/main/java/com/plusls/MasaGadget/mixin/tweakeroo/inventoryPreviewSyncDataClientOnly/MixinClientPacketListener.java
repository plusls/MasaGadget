package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSyncDataClientOnly;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
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
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Inject(method = "handleOpenScreen", at = @At(value = "RETURN"))
    private void postHandleOpenScreen(ClientboundOpenScreenPacket clientboundOpenScreenPacket, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!Configs.inventoryPreviewSyncDataClientOnly ||
                (Configs.inventoryPreviewSyncData && PcaSyncProtocol.enable) ||
                minecraft.hasSingleplayerServer() ||
                !FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() ||
                !Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld()
        ) {
            return;
        }
        minecraft.setScreen(null);
    }
}

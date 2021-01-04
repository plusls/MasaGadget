package com.plusls.MasaGadget.mixin;

import com.plusls.MasaGadget.MasaGadgetMod;
import com.plusls.MasaGadget.network.BborProtocol;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class MixinCustomPayloadS2CPacket implements Packet<ClientPlayPacketListener> {
    @Shadow
    private Identifier channel;

    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void onApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo info) {
        CustomPayloadS2CPacket packet = (CustomPayloadS2CPacket) (Object) this;
        if (channel.getNamespace().equals("bbor")) {
            // 因为 mod 会吞掉包，因此需要手动 mixin 在 mod 处理包之前做处理
            BborProtocol.bborProtocolHandler(channel, packet.getData());

            // 兼容 bbor
            if (!MasaGadgetMod.bborCompat) {
                info.cancel();
            }
        } else if (channel.toString().equals("carpet:structures")) {
            BborProtocol.carpetOrServux = true;
            BborProtocol.enable = false;
        } else if (channel.toString().equals("servux:structures")) {
            BborProtocol.carpetOrServux = true;
            BborProtocol.enable = false;
        }
    }
}

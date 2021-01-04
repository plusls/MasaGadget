package io.github.plusls.MasaGadget.mixin.client;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.github.plusls.MasaGadget.network.ClientNetworkHandler;
import io.github.plusls.MasaGadget.util.ParseBborPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
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

            ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler) clientPlayPacketListener;
            // sb mojang 在 player 创建完成之前，MinecraftClient.getInstance().getNetworkHandler() 永远返回 null
            // 可是连接实际上已经创建了，bbor 为了 bypass 这个机制，使用了这个 trick 来绕过
            //((ClientPlayNetworkHandler) netHandlerPlayClient).sendPacket(SubscribeToServer.getPayload().build());
            PacketByteBuf data = null;
            try {
                data = packet.getData();
                switch (channel.getPath()) {
                    case "initialize": {
                        ClientNetworkHandler.bborInitializeHandler(data);
                        break;
                    }
                    case "add_bounding_box_v2": {
                        ClientNetworkHandler.bborAddBoundingBoxV2Handler(data);
                        break;
                    }
                }
            } finally {
                if (data != null)
                    data.release();
            }
            // 兼容 bbor
            if (!MasaGadgetMod.bborCompat) {
                info.cancel();
            }
        } else if (channel.toString().equals("carpet:structures")) {
            ParseBborPacket.carpetOrservux = true;
            ParseBborPacket.enable = false;
        } else if (channel.toString().equals("servux:structures")) {
            ParseBborPacket.carpetOrservux = true;
            ParseBborPacket.enable = false;
        }
    }
}

package io.github.plusls.MasaGadget.mixin;

import fi.dy.masa.minihud.util.DataStorage;
import io.github.plusls.MasaGadget.MasaGadgetMod;
import io.github.plusls.MasaGadget.util.ParseBborPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
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
    private void preApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo info) {
        CustomPayloadS2CPacket packet = (CustomPayloadS2CPacket) (Object) this;
        String channelName = channel.toString();
        if (channelName.startsWith("bbor:")) {
            PacketByteBuf data = null;
            try {
                data = packet.getData();
                switch (channelName) {
                    case "bbor:initialize": {
                        if (!MasaGadgetMod.bborCompat) {
                            long seed = data.readLong();
                            int spawnX = data.readInt();
                            int spawnZ = data.readInt();
                            // MasaGadgetMod.LOGGER.info(String.format("init seed: %d", seed));
                            // 1.16 以前的版本需要记录 在 minhud reset 后重新加载种子和出生点
                            ParseBborPacket.seedCache = seed;
                            ParseBborPacket.spawnPos = new BlockPos(spawnX, 0, spawnZ);
                            DataStorage.getInstance().setWorldSeed(ParseBborPacket.seedCache);
                            DataStorage.getInstance().setWorldSpawn(ParseBborPacket.spawnPos);
                            ((ClientPlayNetworkHandler) clientPlayPacketListener).sendPacket(MasaGadgetMod.BBOR_SUBSCRIBE_PACKET);
                        }
                        ParseBborPacket.structuresCache = new ListTag();
                        break;
                    }
                    case "bbor:add_bounding_box_v2": {
                        ParseBborPacket.parse(data);
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
        }
    }
}

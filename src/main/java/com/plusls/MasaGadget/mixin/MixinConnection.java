package com.plusls.MasaGadget.mixin;

import com.plusls.MasaGadget.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.RunningOnDifferentThreadException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Connection.class)
public abstract class MixinConnection {
    @Shadow
    private native static <T extends PacketListener> void genericsFtw(Packet<T> packet, PacketListener packetListener);

    @Redirect(
            method = "channelRead0*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V",
                    ordinal = 0
            )
    )
    private <T extends PacketListener> void redirectGenericsFtw(Packet<T> packet, PacketListener packetListener) {
        try {
            MixinConnection.genericsFtw(packet, packetListener);
        } catch (Throwable e) {
            if (!(e instanceof RunningOnDifferentThreadException)) {
                SharedConstants.getLogger().throwing(e);
            }

            throw e;
        }
    }
}

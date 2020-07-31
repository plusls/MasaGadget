package io.github.plusls.MasaGadget.mixin;

import fi.dy.masa.minihud.util.DataStorage;
import io.github.plusls.MasaGadget.util.ParseBborPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
<<<<<<< HEAD
import net.minecraft.world.dimension.DimensionType;
=======
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
>>>>>>> 1.16.x
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRespawnS2CPacket.class)
public abstract class MixinPlayerRespawnS2CPacket {

    @Redirect(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/listener/ClientPlayPacketListener;onPlayerRespawn(Lnet/minecraft/network/packet/s2c/play/PlayerRespawnS2CPacket;)V"))
    void redirectOnPlayerRespawn(ClientPlayPacketListener listener, PlayerRespawnS2CPacket packet) {
<<<<<<< HEAD
        DimensionType oldDimension = ((IMixinClientPlayNetworkHandler) listener).accessor$getClient().player.dimension;
        DimensionType newDimension = packet.getDimension();
=======
        RegistryKey<World> oldDimension = ((IMixinClientPlayNetworkHandler) listener).accessor$getClient().player.world.getRegistryKey();
        RegistryKey<World> newDimension = packet.getDimension();
>>>>>>> 1.16.x
        listener.onPlayerRespawn(packet);
        if (oldDimension != newDimension) {
            // reload minihud struct when dimension change
            DataStorage.getInstance().addOrUpdateStructuresFromServer(ParseBborPacket.structuresCache, 0x7fffffff - 0x1000, false);
        }
    }
}

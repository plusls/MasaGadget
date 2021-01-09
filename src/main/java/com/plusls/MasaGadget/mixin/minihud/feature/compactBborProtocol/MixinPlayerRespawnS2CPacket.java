package com.plusls.MasaGadget.mixin.minihud.feature.compactBborProtocol;

import com.plusls.MasaGadget.minihud.feature.compactBborProtocol.network.BborProtocol;
import fi.dy.masa.minihud.util.DataStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRespawnS2CPacket.class)
public abstract class MixinPlayerRespawnS2CPacket {

    @Redirect(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/listener/ClientPlayPacketListener;onPlayerRespawn(Lnet/minecraft/network/packet/s2c/play/PlayerRespawnS2CPacket;)V"))
    void redirectOnPlayerRespawn(ClientPlayPacketListener listener, PlayerRespawnS2CPacket packet) {
        listener.onPlayerRespawn(packet);
        if (!BborProtocol.enable) {
            return;
        }
        assert MinecraftClient.getInstance().player != null;
        RegistryKey<World> oldDimension = MinecraftClient.getInstance().player.world.getRegistryKey();
        RegistryKey<World> newDimension = packet.getDimension();
        if (oldDimension != newDimension && BborProtocol.structuresCache != null) {
            // reload minihud struct when dimension change
            DataStorage.getInstance().addOrUpdateStructuresFromServer(BborProtocol.structuresCache, 0x7fffffff - 0x1000, false);
        }
    }
}

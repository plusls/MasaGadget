package com.plusls.MasaGadget.mixin.minihud.compactBborProtocol;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.minihud.compactBborProtocol.BborProtocol;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PlayerRespawnS2CPacket.class)
@Dependencies(dependencyList = @Dependency(modId = ModInfo.MINIHUD_MOD_ID, version = "*"))
public abstract class MixinPlayerRespawnS2CPacket {

    @Inject(method = "apply*", at = @At(value = "RETURN"))
    void redirectOnPlayerRespawn(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        if (!Configs.Minihud.COMPACT_BBOR_PROTOCOL.getBooleanValue()) {
            return;
        }
        PlayerRespawnS2CPacket packet = (PlayerRespawnS2CPacket) (Object) this;
        if (!BborProtocol.enable) {
            return;
        }
        RegistryKey<World> oldDimension = Objects.requireNonNull(MinecraftClient.getInstance().player).world.getRegistryKey();
        RegistryKey<World> newDimension = packet.getDimension();
        if (oldDimension != newDimension) {
            // reload minihud struct when dimension change
            // BborProtocol.bborRefreshData();
        }
    }
}

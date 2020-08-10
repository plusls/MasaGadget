package io.github.plusls.MasaGadget.mixin.server;

import io.github.plusls.MasaGadget.network.ServerNetworkHandler;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;


@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler implements PacketListener {
    @Shadow
    public ServerPlayerEntity player;

    // 玩家离线后从 ServerNetworkHandler 中剔除玩家
    @Inject(method = "onDisconnected(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void onDisconnect(Text reason, CallbackInfo ci) {
        UUID playerUuid = player.getUuid();
        ServerNetworkHandler.lastBlockPosMap.remove(playerUuid);
        ServerNetworkHandler.lastEntityUuidMap.remove(playerUuid);
    }
}

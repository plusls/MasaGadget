package io.github.plusls.MasaGadget.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface IMixinClientPlayNetworkHandler {
    @Accessor("client") MinecraftClient accessor$getClient();
}

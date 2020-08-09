package io.github.plusls.MasaGadget.network;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientNetworkHandler {
    public static final Identifier HELLO = MasaGadgetMod.id("hello");

    public static void init() {
        ClientSidePacketRegistry.INSTANCE.register(HELLO, ClientNetworkHandler::helloHandler);
    }

    private static void helloHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        if (!MinecraftClient.getInstance().isIntegratedServerRunning()) {
            MasaGadgetMod.LOGGER.info("MasaGadget detected.");
            MasaGadgetMod.masaGagdetInServer = true;
        }
    }
}

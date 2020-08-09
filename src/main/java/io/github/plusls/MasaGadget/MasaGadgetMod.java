package io.github.plusls.MasaGadget;

import io.github.plusls.MasaGadget.network.ClientNetworkHandler;
import io.github.plusls.MasaGadget.network.ServerNetworkHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class MasaGadgetMod implements ModInitializer, ClientModInitializer {
    public static final String MODID = "masa_gadget_mod";
    public static final Logger LOGGER = LogManager.getLogger("MasaGadgetMod");
    public static boolean bborCompat = false;
    public static CustomPayloadC2SPacket BBOR_SUBSCRIBE_PACKET = null;
    public static String level = "INFO";
    public static boolean masaGagdetInServer = false;

    @Override
    public void onInitialize() {
        Configurator.setLevel(LOGGER.getName(), Level.toLevel(MasaGadgetMod.level));
        ServerNetworkHandler.init();
    }

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("bbor")) {
            LOGGER.info("BBOR detected.");
            bborCompat = true;
        } else {
            BBOR_SUBSCRIBE_PACKET = new CustomPayloadC2SPacket(
                    new Identifier("bbor", "subscribe"),
                    new PacketByteBuf(Unpooled.buffer()));
        }
        ClientNetworkHandler.init();
    }

    public static Identifier id(String id) {
        return new Identifier(MODID, id);
    }
}

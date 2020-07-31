package io.github.plusls.MasaGadget;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MasaGadgetMod implements ModInitializer {
    public static final String MODID = "masa_gadget_mod";
    public static final Logger LOGGER = LogManager.getLogger("MasaGadgetMod");
    public static boolean bborCompat = false;
    public static CustomPayloadC2SPacket BBOR_SUBSCRIBE_PACKET = null;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        // System.out.println("Hello Fabric world!");
        if (FabricLoader.getInstance().isModLoaded("bbor")) {
            LOGGER.info("BBOR detected.");
            bborCompat = true;
        } else {
            BBOR_SUBSCRIBE_PACKET = new CustomPayloadC2SPacket(
                    new Identifier("bbor", "subscribe"),
                    new PacketByteBuf(Unpooled.buffer()));
        }
    }

    public static Identifier id(String id) {
        return new Identifier(MODID, id);
    }
}

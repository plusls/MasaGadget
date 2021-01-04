package com.plusls.MasaGadget;

import com.plusls.MasaGadget.network.BborProtocol;
import com.plusls.MasaGadget.network.PcaSyncProtocol;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class MasaGadgetMod implements ClientModInitializer {
    public static final String MODID = "masa_gadget_mod";
    public static final Logger LOGGER = LogManager.getLogger("MasaGadgetMod");
    public static boolean bborCompat = false;
    public static String level = "INFO";

    @Override
    public void onInitializeClient() {
        Configurator.setLevel(LOGGER.getName(), Level.toLevel(MasaGadgetMod.level));
        if (FabricLoader.getInstance().isModLoaded("bbor")) {
            LOGGER.info("BBOR detected.");
            bborCompat = true;
        }

        // 不需要检查是否存在 Multiconnect，因为 MultiConnectAPI.instance() 是动态获取的
        // 如果 multiconnect 不存在它会 new 一个新的，并且各个 api 的实现是空函数，因此不会有兼容性问题
        // MultiConnectAPI.instance().addServerboundIdentifierCustomPayloadListener(new ServerNetworkHandler());
        // MultiConnectAPI.instance().addIdentifierCustomPayloadListener(new ClientNetworkHandler());
        BborProtocol.init();
        PcaSyncProtocol.init();
    }

    public static Identifier id(String id) {
        return new Identifier(MODID, id);
    }
}

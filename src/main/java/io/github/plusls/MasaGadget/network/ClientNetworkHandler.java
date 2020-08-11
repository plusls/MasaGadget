package io.github.plusls.MasaGadget.network;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.World;

public class ClientNetworkHandler {
    public static final Identifier HELLO = MasaGadgetMod.id("hello");
    public static final Identifier RESPONSE_ENTITY = MasaGadgetMod.id("response_entity");

    public static void init() {
        ClientSidePacketRegistry.INSTANCE.register(HELLO, ClientNetworkHandler::helloHandler);
        ClientSidePacketRegistry.INSTANCE.register(RESPONSE_ENTITY, ClientNetworkHandler::responseEntityHandler);
    }

    private static void helloHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        if (!MinecraftClient.getInstance().isIntegratedServerRunning()) {
            MasaGadgetMod.LOGGER.info("MasaGadget detected.");
            MasaGadgetMod.masaGagdetInServer = true;
        }
    }

    // 反序列化实体数据
    private static void responseEntityHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        PlayerEntity player = packetContext.getPlayer();
        World world = player.world;
        int entityId = packetByteBuf.readInt();
        CompoundTag tag = packetByteBuf.readCompoundTag();
        Entity entity = world.getEntityById(entityId);
        if (entity != null) {
            MasaGadgetMod.LOGGER.debug("update entity!");
            // 1.14 特有麻烦
            try {
                entity.fromTag(tag);
            } catch (CrashException e) {
            }
        }
    }
}

package com.plusls.MasaGadget.mixin.generic.autoSyncEntityData;

import com.google.common.collect.ImmutableList;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.accessor.AccessorVillager;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPacketListener.class)
public class MixinClientPlayNetworkHandler {

    private static final List<SoundEvent> WORK_SOUNDS = ImmutableList.of(
            SoundEvents.VILLAGER_WORK_ARMORER,
            SoundEvents.VILLAGER_WORK_BUTCHER,
            SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
            SoundEvents.VILLAGER_WORK_CLERIC,
            SoundEvents.VILLAGER_WORK_FARMER,
            SoundEvents.VILLAGER_WORK_FISHERMAN,
            SoundEvents.VILLAGER_WORK_FLETCHER,
            SoundEvents.VILLAGER_WORK_LEATHERWORKER,
            SoundEvents.VILLAGER_WORK_LIBRARIAN,
            SoundEvents.VILLAGER_WORK_MASON,
            SoundEvents.VILLAGER_WORK_SHEPHERD,
            SoundEvents.VILLAGER_WORK_TOOLSMITH,
            SoundEvents.VILLAGER_WORK_WEAPONSMITH
    );

    @Inject(method = "handleSoundEvent", at = @At(value = "RETURN"))
    private void syncVillagerData(ClientboundSoundPacket packet, CallbackInfo ci) {
        if (!Configs.autoSyncEntityData || Minecraft.getInstance().hasSingleplayerServer() || !PcaSyncProtocol.enable) {
            return;
        }
        ClientLevel clientWorld = Minecraft.getInstance().level;
        if (clientWorld == null || (!WORK_SOUNDS.contains(packet.getSound()))) {
            return;
        }
        // 工作后可能会发生补货，因此在播放工作声音后需要同步村民数据
        clientWorld.getEntitiesOfClass(Villager.class,
                new AABB(packet.getX() - 1, packet.getY() - 1, packet.getZ() - 1, packet.getX() + 1, packet.getY() + 1, packet.getZ() + 1),
                villager -> ((AccessorVillager) villager).invokeNeedsToRestock()).forEach(
                villagerEntity -> {
                    PcaSyncProtocol.syncEntity(villagerEntity.getId());
                    PcaSyncProtocol.cancelSyncEntity();
                }
        );

    }
}

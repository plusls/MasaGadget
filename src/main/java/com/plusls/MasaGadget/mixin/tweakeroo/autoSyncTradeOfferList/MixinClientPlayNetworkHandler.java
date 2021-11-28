package com.plusls.MasaGadget.mixin.tweakeroo.autoSyncTradeOfferList;

import com.google.common.collect.ImmutableList;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.tweakeroo.pcaSyncProtocol.PcaSyncProtocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Dependencies(dependencyList = @Dependency(modId = ModInfo.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    private static final List<SoundEvent> WORK_SOUNDS = ImmutableList.of(
            SoundEvents.ENTITY_VILLAGER_WORK_ARMORER,
            SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER,
            SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER,
            SoundEvents.ENTITY_VILLAGER_WORK_CLERIC,
            SoundEvents.ENTITY_VILLAGER_WORK_FARMER,
            SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN,
            SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER,
            SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER,
            SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN,
            SoundEvents.ENTITY_VILLAGER_WORK_MASON,
            SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD,
            SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH,
            SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH
    );

    @Inject(method = "onPlaySound", at = @At(value = "RETURN"))
    private void syncVillagerData(PlaySoundS2CPacket packet, CallbackInfo ci) {
        if (!Configs.Tweakeroo.AUTO_SYNC_TRADE_OFFER_LIST.getDefaultBooleanValue() || MinecraftClient.getInstance().isIntegratedServerRunning() || !PcaSyncProtocol.enable) {
            return;
        }
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld == null || (!WORK_SOUNDS.contains(packet.getSound()))) {
            return;
        }
        // 工作后可能会发生补货，因此在播放工作声音后需要同步村民数据
        clientWorld.getEntitiesByClass(VillagerEntity.class,
                new Box(packet.getX() - 1, packet.getY() - 1, packet.getZ() - 1, packet.getX() + 1, packet.getY() + 1, packet.getZ() + 1),
                VillagerEntity::needsRestock).forEach(
                villagerEntity -> {
                    PcaSyncProtocol.syncEntity(villagerEntity.getEntityId());
                    PcaSyncProtocol.cancelSyncEntity();
                }
        );

    }
}

package com.plusls.MasaGadget.mixin.tweakeroo.autoSyncTradeOfferList;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.tweakeroo.pcaSyncProtocol.PcaSyncProtocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillagerEntity.class)
public abstract class MixinZombieVillagerEntity extends ZombieEntity implements VillagerDataContainer {
    public MixinZombieVillagerEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "handleStatus", at = @At(value = "RETURN"))
    private void syncVillagerData(byte status, CallbackInfo ci) {
        if (!Configs.Tweakeroo.AUTO_SYNC_TRADE_OFFER_LIST.getDefaultBooleanValue() || MinecraftClient.getInstance().isIntegratedServerRunning() || !PcaSyncProtocol.enable) {
            return;
        }
        if (status == 16) {
            PcaSyncProtocol.syncEntity(this.getId());
            PcaSyncProtocol.cancelSyncEntity();
        }
    }
}

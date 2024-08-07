package com.plusls.MasaGadget.mixin.feature.autoSyncEntityData;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.EntityCompat;

@Mixin(ZombieVillager.class)
public abstract class MixinZombieVillagerEntity extends Zombie {
    public MixinZombieVillagerEntity(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract boolean isConverting();

    @Shadow
    protected abstract int getConversionProgress();

    @Shadow
    private int villagerConversionTime;

    @Inject(method = "handleEntityEvent", at = @At("RETURN"))
    private void syncVillagerData(byte status, CallbackInfo ci) {
        if (!Configs.autoSyncEntityData.getBooleanValue() ||
                Minecraft.getInstance().hasSingleplayerServer() ||
                !PcaSyncProtocol.enable) {
            return;
        }

        if (status == 16) {
            PcaSyncProtocol.syncEntity(this.getId());
            PcaSyncProtocol.cancelSyncEntity();
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void syncConvertingData(CallbackInfo ci) {
        if (EntityCompat.of(this).getLevel().isClientSide() && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();
            this.villagerConversionTime -= i;

            if (this.villagerConversionTime <= 0) {
                // 如果这里为负，应该是没有同步数据
                if (!Configs.autoSyncEntityData.getBooleanValue() ||
                        Minecraft.getInstance().hasSingleplayerServer() ||
                        !PcaSyncProtocol.enable) {
                    return;
                }

                PcaSyncProtocol.syncEntity(this.getId());
                PcaSyncProtocol.cancelSyncEntity();
            }
        }
    }
}

package com.plusls.MasaGadget.mixin.generic.autoSyncEntityData;

import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillager.class)
public abstract class MixinZombieVillagerEntity extends Zombie {


    public MixinZombieVillagerEntity(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "handleEntityEvent", at = @At(value = "RETURN"))
    private void syncVillagerData(byte status, CallbackInfo ci) {
        if (!Configs.autoSyncEntityData || Minecraft.getInstance().hasSingleplayerServer() || !PcaSyncProtocol.enable) {
            return;
        }
        if (status == 16) {
            PcaSyncProtocol.syncEntity(this.getId());
            PcaSyncProtocol.cancelSyncEntity();
        }
    }
}

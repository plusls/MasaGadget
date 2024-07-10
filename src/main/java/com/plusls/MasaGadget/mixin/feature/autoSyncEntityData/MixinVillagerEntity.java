package com.plusls.MasaGadget.mixin.feature.autoSyncEntityData;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class MixinVillagerEntity {

    private VillagerProfession oldVillagerProfession;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void syncVillagerData(CallbackInfo ci) {
        if (!Configs.autoSyncEntityData.getBooleanValue() ||
                Minecraft.getInstance().hasSingleplayerServer() ||
                !PcaSyncProtocol.enable) {
            return;
        }
        VillagerProfession currentVillagerProfession = ((Villager) (Object) this).getVillagerData().getProfession();
        if (oldVillagerProfession != currentVillagerProfession) {
            PcaSyncProtocol.syncEntity(((Villager) (Object) this).getId());
            PcaSyncProtocol.cancelSyncEntity();
            oldVillagerProfession = currentVillagerProfession;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "handleEntityEvent", at = @At(value = "RETURN"))
    private void syncVillagerData(byte status, CallbackInfo ci) {
        if (!Configs.autoSyncEntityData.getBooleanValue() ||
                Minecraft.getInstance().hasSingleplayerServer() ||
                !PcaSyncProtocol.enable) {
            return;
        }
        if (status == 14) {
            PcaSyncProtocol.syncEntity(((Villager) (Object) this).getId());
            PcaSyncProtocol.cancelSyncEntity();
        }
    }


}

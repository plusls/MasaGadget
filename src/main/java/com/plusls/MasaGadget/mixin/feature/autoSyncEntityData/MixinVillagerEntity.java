package com.plusls.MasaGadget.mixin.feature.autoSyncEntityData;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class MixinVillagerEntity {
    @Unique
    private VillagerProfession masa_gadget_mod$oldVillagerProfession;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("RETURN"))
    private void syncVillagerData(CallbackInfo ci) {
        if (!Configs.autoSyncEntityData.getBooleanValue() ||
                Minecraft.getInstance().hasSingleplayerServer() ||
                !PcaSyncProtocol.enable) {
            return;
        }

        VillagerProfession currentVillagerProfession = ((Villager) (Object) this).getVillagerData().getProfession();

        if (this.masa_gadget_mod$oldVillagerProfession != currentVillagerProfession) {
            PcaSyncProtocol.syncEntity(((Villager) (Object) this).getId());
            PcaSyncProtocol.cancelSyncEntity();
            this.masa_gadget_mod$oldVillagerProfession = currentVillagerProfession;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "handleEntityEvent", at = @At("RETURN"))
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

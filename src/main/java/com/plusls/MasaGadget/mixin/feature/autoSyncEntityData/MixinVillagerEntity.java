package com.plusls.MasaGadget.mixin.feature.autoSyncEntityData;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import com.plusls.MasaGadget.util.VillagerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC > 12104
//$$ import net.minecraft.resources.ResourceKey;
//#endif

@Mixin(Villager.class)
public abstract class MixinVillagerEntity {
    @Unique
    //#if MC > 12104
    //$$ private ResourceKey<VillagerProfession> masa_gadget_mod$oldVillagerProfession;
    //#else
    private VillagerProfession masa_gadget_mod$oldVillagerProfession;
    //#endif

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("RETURN"))
    private void syncVillagerData(CallbackInfo ci) {
        if (!Configs.autoSyncEntityData.getBooleanValue() ||
                Minecraft.getInstance().hasSingleplayerServer() ||
                !PcaSyncProtocol.enable) {
            return;
        }

        //#if MC > 12104
        //$$ ResourceKey<VillagerProfession> currentVillagerProfession = VillagerDataUtil.getVillagerProfession((Villager) (Object) this);
        //#else
        VillagerProfession currentVillagerProfession = VillagerDataUtil.getVillagerProfession((Villager) (Object) this);
        //#endif

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

package com.plusls.MasaGadget.util;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

//#if MC > 12104
//$$ import net.minecraft.resources.ResourceKey;
//#endif

public class VillagerDataUtil {
    //#if MC > 12104
    //$$ public static ResourceKey<VillagerProfession> getVillagerProfession(Villager villager) {
    //$$     return villager.getVillagerData().profession().unwrapKey().orElse(null);
    //$$ }
    //#else
    public static VillagerProfession getVillagerProfession(Villager villager) {
        return villager.getVillagerData().getProfession();
    }
    //#endif
}

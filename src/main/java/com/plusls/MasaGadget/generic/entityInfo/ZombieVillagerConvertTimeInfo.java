package com.plusls.MasaGadget.generic.entityInfo;

import com.plusls.MasaGadget.mixin.accessor.AccessorZombieVillager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.compat.minecraft.api.network.chat.ComponentCompatApi;

public class ZombieVillagerConvertTimeInfo {
    public static @NotNull Component getInfo(ZombieVillager zombieVillager) {
        int villagerConversionTime = ((AccessorZombieVillager) zombieVillager).getVillagerConversionTime();

        if (villagerConversionTime > 0) {
            return ComponentCompatApi.literal(String.format("%d", villagerConversionTime));
        }

        return ComponentCompatApi.literal("-1");
    }
}

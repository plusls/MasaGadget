package com.plusls.MasaGadget.impl.feature.entityInfo;

import com.plusls.MasaGadget.mixin.accessor.AccessorZombieVillager;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;

public class ZombieVillagerConvertTimeInfo {
    public static @NotNull Component getInfo(ZombieVillager zombieVillager) {
        if (!Minecraft.getInstance().hasSingleplayerServer() && !PcaSyncProtocol.enable) {
            return ComponentUtil.tr("masa_gadget_mod.message.no_data").withStyle(ChatFormatting.YELLOW).get();
        }

        int villagerConversionTime = ((AccessorZombieVillager) zombieVillager).masa_gadget_mod$$getVillagerConversionTime();

        if (villagerConversionTime > 0) {
            return ComponentUtil.simple(String.format("%d", villagerConversionTime)).get();
        }

        return ComponentUtil.simple("-1").get();
    }
}

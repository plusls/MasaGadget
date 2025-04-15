package com.plusls.MasaGadget.impl.feature.entityInfo;

import com.plusls.MasaGadget.mixin.accessor.AccessorVillager;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import com.plusls.MasaGadget.util.VillagerDataUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;

//#if MC > 12104
//$$ import net.minecraft.resources.ResourceKey;
//#endif

public class VillagerNextRestockTimeInfo {
    public static Component getInfo(@NotNull Villager villager) {
        long nextRestockTime;
        long nextWorkTime;
        long timeOfDay = villager.getLevel().getDayTime() % 24000;

        //#if MC > 12104
        //$$ ResourceKey<VillagerProfession> profession = VillagerDataUtil.getVillagerProfession(villager);
        //#else
        VillagerProfession profession = VillagerDataUtil.getVillagerProfession(villager);
        //#endif

        if (profession == VillagerProfession.NONE || profession == VillagerProfession.NITWIT) {
            return null;
        }

        if (!Minecraft.getInstance().hasSingleplayerServer() && !PcaSyncProtocol.enable) {
            return ComponentUtil.tr("masa_gadget_mod.message.no_data").withStyle(ChatFormatting.YELLOW);
        }

        if (timeOfDay >= 2000 && timeOfDay <= 9000) {
            nextWorkTime = 0;
        } else {
            nextWorkTime = timeOfDay < 2000 ? 2000 - timeOfDay : 24000 - timeOfDay + 2000;
        }

        int numberOfRestocksToday = ((AccessorVillager) villager).masa_gadget_mod$getNumberOfRestocksToday();
        long lastRestockGameTime = ((AccessorVillager) villager).masa_gadget_mod$getLastRestockGameTime();

        if (numberOfRestocksToday == 0) {
            nextRestockTime = 0;
        } else if (numberOfRestocksToday < 2) {
            nextRestockTime = Math.max(lastRestockGameTime + 2400 - villager.getLevel().getGameTime(), 0);
        } else {
            nextRestockTime = 0x7fffffffffffffffL;
        }

        nextRestockTime = Math.min(nextRestockTime, Math.max(lastRestockGameTime + 12000L - villager.getLevel().getGameTime(), 0));

        if (needsRestock(villager)) {
            if (timeOfDay + nextRestockTime > 8000) {
                // cd 好的时候村民已经不再工作了
                nextRestockTime = 24000 - timeOfDay + 2000;
            } else {
                nextRestockTime = Math.max(nextRestockTime, nextWorkTime);
            }
        } else {
            nextRestockTime = 0;
        }

        if (nextRestockTime == 0) {
            return ComponentUtil.simple("OK").withStyle(ChatFormatting.GREEN);
        }

        return ComponentUtil.simple(String.format("%d", nextRestockTime));
    }

    // 因为刁民的需要补货的函数，会检查当前货物是否被消耗，从使用的角度只需要关心当前货物是否用完
    private static boolean needsRestock(@NotNull Villager villager) {
        if (VillagerDataUtil.getVillagerProfession(villager) != VillagerProfession.NONE) {
            for (MerchantOffer offer : villager.getOffers()) {
                if (offer.isOutOfStock()) {
                    return true;
                }
            }
        }

        return false;
    }
}

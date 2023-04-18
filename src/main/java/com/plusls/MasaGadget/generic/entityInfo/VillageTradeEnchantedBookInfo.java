package com.plusls.MasaGadget.generic.entityInfo;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.compat.minecraft.api.network.chat.ComponentCompatApi;

import java.util.List;
import java.util.Map;

public class VillageTradeEnchantedBookInfo {
    public static @NotNull List<Component> getInfo(@NotNull Villager villager) {
        List<Component> ret = Lists.newArrayList();

        if (villager.getVillagerData().getProfession() != VillagerProfession.LIBRARIAN) {
            return ret;
        }

        for (MerchantOffer tradeOffer : villager.getOffers()) {
            ItemStack sellItem = tradeOffer.getResult();

            if (sellItem.is(Items.ENCHANTED_BOOK)) {
                Map<Enchantment, Integer> enchantmentData = EnchantmentHelper.getEnchantments(sellItem);

                for (Map.Entry<Enchantment, Integer> entry : enchantmentData.entrySet()) {
                    int level = entry.getValue();
                    int cost = tradeOffer.getBaseCostA().getCount();
                    int minCost = 2 + 3 * level;
                    int maxCost = minCost + 4 + level * 10;

                    if (entry.getKey().isTreasureOnly()) {
                        minCost *= 2;
                        maxCost *= 2;
                    }

                    ChatFormatting color;

                    if (cost <= (maxCost - minCost) / 3 + minCost) {
                        color = ChatFormatting.GREEN;
                    } else if (cost <= (maxCost - minCost) / 3 * 2 + minCost) {
                        color = ChatFormatting.WHITE;
                    } else {
                        color = ChatFormatting.RED;
                    }

                    if (level == entry.getKey().getMaxLevel()) {
                        //#if MC > 11502
                        ret.add(((MutableComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.GOLD));
                        //#else
                        //$$ ret.add(((BaseComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.GOLD));
                        //#endif
                    } else {
                        //#if MC > 11502
                        ret.add(((MutableComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.WHITE));
                        //#else
                        //$$ ret.add(((BaseComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.GOLD));
                        //#endif
                    }

                    ret.add(ComponentCompatApi.literal(String.format("%d(%d-%d)", cost, minCost, maxCost)).withStyle(color));
                }

                break;
            }
        }

        return ret;
    }
}

package com.plusls.MasaGadget.impl.feature.entityInfo;

import com.google.common.collect.Lists;
import com.plusls.MasaGadget.util.PcaSyncProtocol;
import com.plusls.MasaGadget.util.VillagerDataUtil;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.api.compat.minecraft.world.item.ItemStackCompat;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;

// CHECKSTYLE.OFF: ImportOrder
//#if MC > 12004
//$$ import it.unimi.dsi.fastutil.objects.Object2IntMap;
//#endif
// CHECKSTYLE.ON: ImportOrder

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;

// CHECKSTYLE.OFF: ImportOrder
//#if MC > 12006
//$$ import net.minecraft.tags.EnchantmentTags;
//#endif

//#if MC > 12004
//$$ import net.minecraft.core.Holder;
//$$ import net.minecraft.world.item.enchantment.ItemEnchantments;
//#endif

//#if MC > 11502
import net.minecraft.network.chat.MutableComponent;
//#endif
// CHECKSTYLE.ON: ImportOrder

import java.util.Collections;
import java.util.List;

// CHECKSTYLE.OFF: ImportOrder
//#if MC < 12005
import java.util.Map;
//#endif
// CHECKSTYLE.ON: ImportOrder

public class VillagerTradeEnchantedBookInfo {
    public static @NotNull List<Component> getInfo(@NotNull Villager villager) {
        if (VillagerDataUtil.getVillagerProfession(villager) != VillagerProfession.LIBRARIAN) {
            return Collections.emptyList();
        }

        if (!Minecraft.getInstance().hasSingleplayerServer() && !PcaSyncProtocol.enable) {
            return Lists.newArrayList(ComponentUtil.tr("masa_gadget_mod.message.no_data").withStyle(ChatFormatting.YELLOW));
        }

        List<Component> ret = Lists.newArrayList();

        for (MerchantOffer tradeOffer : villager.getOffers()) {
            ItemStack sellItem = tradeOffer.getResult();
            ItemStackCompat sellItemCompat = ItemStackCompat.of(sellItem);

            if (!sellItemCompat.is(Items.ENCHANTED_BOOK)) {
                continue;
            }

            //#if MC > 12004
            //$$ ItemEnchantments enchantmentData = EnchantmentHelper.getEnchantmentsForCrafting(sellItem);
            //#else
            Map<Enchantment, Integer> enchantmentData = EnchantmentHelper.getEnchantments(sellItem);
            //#endif

            for (
                    // CHECKSTYLE.OFF: Indentation
                    //#if MC > 12004
                    //$$ Object2IntMap.Entry<Holder<Enchantment>> entry: enchantmentData.entrySet()
                    //#else
                    Map.Entry<Enchantment, Integer> entry : enchantmentData.entrySet()
                    //#endif
                    // CHECKSTYLE.ON: Indentation
            ) {
                //#if MC > 12004
                //$$ int level = entry.getIntValue();
                //#else
                int level = entry.getValue();
                //#endif
                int cost = tradeOffer.getBaseCostA().getCount();
                int minCost = 2 + 3 * level;
                int maxCost = minCost + 4 + level * 10;

                if (
                        //#if MC > 12006
                        //$$ entry.getKey().is(EnchantmentTags.DOUBLE_TRADE_PRICE)
                        //#elseif MC > 12004
                        //$$ entry.getKey().value().isTreasureOnly()
                        //#else
                        entry.getKey().isTreasureOnly()
                        //#endif
                ) {
                    minCost *= 2;
                    maxCost *= 2;
                }

                ChatFormatting cast_color;
                int one_third = (maxCost - minCost) / 3;

                if (cost <= one_third + minCost) {
                    cast_color = ChatFormatting.GREEN;
                } else if (cost <= one_third * 2 + minCost) {
                    cast_color = ChatFormatting.WHITE;
                } else {
                    cast_color = ChatFormatting.RED;
                }

                ChatFormatting enchantment_level_color;

                if (
                        //#if MC > 12004
                        //$$ level == entry.getKey().value().getMaxLevel()
                        //#else
                        level == entry.getKey().getMaxLevel()
                        //#endif
                ) {
                    enchantment_level_color = ChatFormatting.GOLD;
                } else {
                    enchantment_level_color = ChatFormatting.WHITE;
                }

                //#if MC > 12006
                //$$ ret.add(((MutableComponent) Enchantment.getFullname(entry.getKey(), entry.getIntValue())).withStyle(enchantment_level_color));
                //#elseif MC > 12004
                //$$ ret.add(((MutableComponent) entry.getKey().value().getFullname(entry.getIntValue())).withStyle(enchantment_level_color));
                //#elseif MC > 11502
                ret.add(((MutableComponent) entry.getKey().getFullname(entry.getValue())).withStyle(enchantment_level_color));
                //#else
                //$$ ret.add((entry.getKey().getFullname(entry.getValue())).withStyle(enchantment_level_color));
                //#endif
                ret.add(ComponentUtil.simple(String.format("%d(%d-%d)", cost, minCost, maxCost)).withStyle(cast_color));
            }

            break;
        }

        return ret;
    }
}

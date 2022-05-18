package com.plusls.MasaGadget.generic.renderTradeEnchantedBook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.RenderEvent;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.ComponentCompatApi;

import java.util.Map;

public class TradeEnchantedBookRenderer {

    public static void init() {
        RenderEvent.register(TradeEnchantedBookRenderer::postRenderEntity);
    }

    private static void postRenderEntity(EntityRenderDispatcher dispatcher, Entity entity, float yaw, float tickDelta,
                                         PoseStack matrixStack, int light) {
        if (!(entity instanceof Villager) || !Configs.renderTradeEnchantedBook) {
            return;
        }
        Villager villagerEntity = MiscUtil.getBestEntity((Villager) entity);
        if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.LIBRARIAN) {
            return;
        }
        Component text = null;
        Component price = null;

        for (MerchantOffer tradeOffer : villagerEntity.getOffers()) {
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
                    price = ComponentCompatApi.literal(String.format("%d(%d-%d)", cost, minCost, maxCost)).withStyle(color);

                    if (level == entry.getKey().getMaxLevel()) {
                        //#if MC > 11502
                        text = ((MutableComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.GOLD);
                        //#else
                        //$$ text = ((BaseComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.GOLD);
                        //#endif
                    } else {
                        //#if MC > 11502
                        text = ((MutableComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.WHITE);
                        //#else
                        //$$ text = ((BaseComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.GOLD);
                        //#endif
                    }
                }
            }
            if (text != null) {
                break;
            }
        }
        if (text == null) {
            return;
        }

        RenderUtil.renderTextOnEntity(matrixStack, villagerEntity, dispatcher, text,
                villagerEntity.getBbHeight() / 8 * 7, false);

        RenderUtil.renderTextOnEntity(matrixStack, villagerEntity, dispatcher, price,
                villagerEntity.getBbHeight() / 8 * 7 - 11 * 0.018F, false);


    }
}

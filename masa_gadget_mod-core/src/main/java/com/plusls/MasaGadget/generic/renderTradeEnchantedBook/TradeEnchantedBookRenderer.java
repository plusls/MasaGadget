package com.plusls.MasaGadget.generic.renderTradeEnchantedBook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.RenderEvent;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Map;

public class TradeEnchantedBookRenderer {

    public static void init() {
        RenderEvent.register(TradeEnchantedBookRenderer::postRenderEntity);
    }

    private static void postRenderEntity(EntityRenderDispatcher dispatcher, Entity entity, float yaw, float tickDelta,
                                         PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
        if (!(entity instanceof Villager) || !Configs.renderTradeEnchantedBook) {
            return;
        }
        Villager villagerEntity = MiscUtil.getBestEntity((Villager) entity);

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
                    price = new TextComponent(String.format("%d(%d-%d)", cost, minCost, maxCost)).withStyle(color);

                    if (level == entry.getKey().getMaxLevel()) {
                        text = ((TranslatableComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.GOLD);
                    } else {
                        text = ((TranslatableComponent) entry.getKey().getFullname(entry.getValue())).withStyle(ChatFormatting.WHITE);
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

        RenderUtil.renderTextOnEntity(matrixStack, villagerEntity, dispatcher, vertexConsumerProvider, text,
                villagerEntity.getBbHeight() / 8 * 7);

        RenderUtil.renderTextOnEntity(matrixStack, villagerEntity, dispatcher, vertexConsumerProvider, price,
                villagerEntity.getBbHeight() / 8 * 7 - 11 * 0.018F);


    }
}

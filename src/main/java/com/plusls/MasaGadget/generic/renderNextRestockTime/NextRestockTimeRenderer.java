package com.plusls.MasaGadget.generic.renderNextRestockTime;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.event.RenderEvent;
import com.plusls.MasaGadget.mixin.accessor.AccessorVillager;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.trading.MerchantOffer;

public class NextRestockTimeRenderer {
    public static void init() {
        RenderEvent.register(NextRestockTimeRenderer::postRenderEntity);
    }

    private static void postRenderEntity(EntityRenderDispatcher dispatcher, Entity entity, float yaw, float tickDelta, PoseStack matrixStack,
                                         MultiBufferSource vertexConsumerProvider, int light) {
        if (!(entity instanceof Villager) || !Configs.renderNextRestockTime) {
            return;
        }
        Villager villagerEntity = MiscUtil.getBestEntity((Villager) entity);

        Component text;

        long nextRestockTime;
        long nextWorkTime;
        long timeOfDay = villagerEntity.getLevel().getDayTime() % 24000;
        if (timeOfDay >= 2000 && timeOfDay <= 9000) {
            nextWorkTime = 0;
        } else {
            nextWorkTime = timeOfDay < 2000 ? 2000 - timeOfDay : 24000 - timeOfDay + 2000;
        }

        int numberOfRestocksToday = ((AccessorVillager) villagerEntity).getNumberOfRestocksToday();
        long lastRestockGameTime = ((AccessorVillager) villagerEntity).getLastRestockGameTime();

        if (numberOfRestocksToday == 0) {
            nextRestockTime = 0;
        } else if (numberOfRestocksToday < 2) {
            nextRestockTime = Math.max(lastRestockGameTime + 2400 - villagerEntity.getLevel().getGameTime(), 0);
        } else {
            nextRestockTime = 0x7fffffffffffffffL;
        }

        nextRestockTime = Math.min(nextRestockTime, Math.max(lastRestockGameTime + 12000L - villagerEntity.getLevel().getGameTime(), 0));


        if (needsRestock(villagerEntity)) {
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
            text = new TextComponent("OK").withStyle(ChatFormatting.GREEN);
        } else {
            text = new TextComponent(String.format("%d", nextRestockTime));
        }
        RenderUtil.renderTextOnEntity(matrixStack, villagerEntity,
                dispatcher, vertexConsumerProvider, text,
                villagerEntity.getBbHeight() / 32 * 31, false);
    }

    // 因为刁民的需要补货的函数，会检查当前货物是否被消耗，从使用的角度只需要关心当前货物是否用完
    private static boolean needsRestock(Villager villagerEntity) {
        for (MerchantOffer offer : villagerEntity.getOffers()) {
            if (offer.isOutOfStock()) {
                return true;
            }
        }
        return false;
    }
}

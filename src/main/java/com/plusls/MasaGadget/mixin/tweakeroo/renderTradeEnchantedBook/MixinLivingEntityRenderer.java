package com.plusls.MasaGadget.mixin.tweakeroo.renderTradeEnchantedBook;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.util.MiscUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Dependencies(dependencyList = @Dependency(modId = ModInfo.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity> extends EntityRenderer<T> {

    protected MixinLivingEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    // from entityRenderer
    @Inject(method = "render*", at = @At(value = "RETURN"))
    private void postRenderEntity(T livingEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (!(livingEntity instanceof VillagerEntity) || !Configs.Tweakeroo.RENDER_TRADE_ENCHANTED_BOOK.getBooleanValue()) {
            return;
        }

        VillagerEntity villagerEntity = MiscUtil.getBestEntity((VillagerEntity) livingEntity);

        Text text = null;
        Text price = null;

        for (TradeOffer tradeOffer : villagerEntity.getOffers()) {
            ItemStack sellItem = tradeOffer.getSellItem();
            if (sellItem.getItem() == Items.ENCHANTED_BOOK) {
                Map<Enchantment, Integer> enchantmentData = EnchantmentHelper.get(sellItem);
                for (Map.Entry<Enchantment, Integer> entry : enchantmentData.entrySet()) {
                    int level = entry.getValue();
                    int cost = tradeOffer.getOriginalFirstBuyItem().getCount();
                    int minCost = 2 + 3 * level;
                    int maxCost = minCost + 4 + level * 10;
                    if (entry.getKey().isTreasure()) {
                        minCost *= 2;
                        maxCost *= 2;
                    }
                    Formatting color;
                    if (cost <= (maxCost - minCost) / 3 + minCost) {
                        color = Formatting.GREEN;
                    } else if (cost <= (maxCost - minCost) / 3 * 2 + minCost) {
                        color = Formatting.WHITE;
                    } else {
                        color = Formatting.RED;
                    }
                    price = new LiteralText(String.format("%d(%d-%d)", cost, minCost, maxCost)).formatted(color);

                    if (level == entry.getKey().getMaxLevel()) {
                        text = ((MutableText) entry.getKey().getName(entry.getValue())).formatted(Formatting.GOLD);
                    } else {
                        text = ((MutableText) entry.getKey().getName(entry.getValue())).formatted(Formatting.WHITE);
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

        RenderUtil.renderTextOnEntity(matrixStack, villagerEntity, this.dispatcher, vertexConsumerProvider, text,
                villagerEntity.getHeight() / 8 * 7);

        RenderUtil.renderTextOnEntity(matrixStack, villagerEntity, this.dispatcher, vertexConsumerProvider, price,
                villagerEntity.getHeight() / 8 * 7 - 11 * 0.018F);


    }
}

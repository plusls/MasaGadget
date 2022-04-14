package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.tweakeroo.TraceUtil;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = RenderUtils.class, remap = false)
public class MixinRenderUtils {

    private static final int MAX_TRADE_OFFER_SIZE = 9;

    @Inject(method = "renderInventoryOverlay", at = @At(value = "RETURN"))
    private static void renderTradeOfferList(Minecraft mc, PoseStack matrixStack, CallbackInfo ci) {
        if (!Configs.inventoryPreviewSupportTradeOfferList) {
            return;
        }
        Entity entity = TraceUtil.getTraceEntity();
        if (!(entity instanceof AbstractVillager)) {
            return;
        }
        SimpleContainer simpleInventory = new SimpleContainer(MAX_TRADE_OFFER_SIZE);
        for (MerchantOffer tradeOffer : ((AbstractVillager) entity).getOffers()) {
            for (int i = 0; i < simpleInventory.getContainerSize(); ++i) {
                ItemStack itemStack = simpleInventory.getItem(i);
                if (itemStack.isEmpty()) {
                    simpleInventory.setItem(i, tradeOffer.getResult().copy());
                    break;
                }
            }
        }
        int x = GuiUtils.getScaledWindowWidth() / 2 - 88;
        int y = GuiUtils.getScaledWindowHeight() / 2 - 5;
        int slotOffsetX = 8;
        int slotOffsetY = 8;
        InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.GENERIC;
        DyeColor dye = DyeColor.GREEN;
        float[] colors = dye.getTextureDiffuseColors();

        fi.dy.masa.malilib.render.RenderUtils.color(colors[0], colors[1], colors[2], 1.0F);
        InventoryOverlay.renderInventoryBackground(type, x, y, MAX_TRADE_OFFER_SIZE, MAX_TRADE_OFFER_SIZE, Minecraft.getInstance());
        InventoryOverlay.renderInventoryStacks(type, simpleInventory, x + slotOffsetX, y + slotOffsetY, MAX_TRADE_OFFER_SIZE, 0, MAX_TRADE_OFFER_SIZE, mc);
        fi.dy.masa.malilib.render.RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

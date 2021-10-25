package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.tweakeroo.TraceUtil;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = RenderUtils.class, remap = false)
public class MixinRenderUtils {

    private static final int MAX_TRADE_OFFER_SIZE = 9;

    @Inject(method = "renderInventoryOverlay", at = @At(value = "RETURN"))
    private static void renderTradeOfferList(MinecraftClient mc, MatrixStack matrixStack, CallbackInfo ci) {
        if (!Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_TRADE_OFFER_LIST.getBooleanValue()) {
            return;
        }
        Entity entity = TraceUtil.getTraceEntity();
        if (!(entity instanceof MerchantEntity)) {
            return;
        }
        SimpleInventory simpleInventory = new SimpleInventory(MAX_TRADE_OFFER_SIZE);
        for (TradeOffer tradeOffer : ((MerchantEntity) entity).getOffers()) {
            for (int i = 0; i < simpleInventory.size(); ++i) {
                ItemStack itemStack = simpleInventory.getStack(i);
                if (itemStack.isEmpty()) {
                    simpleInventory.setStack(i, tradeOffer.getSellItem().copy());
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
        float[] colors = dye.getColorComponents();

        fi.dy.masa.malilib.render.RenderUtils.color(colors[0], colors[1], colors[2], 1.0F);
        InventoryOverlay.renderInventoryBackground(type, x, y, MAX_TRADE_OFFER_SIZE, MAX_TRADE_OFFER_SIZE, MinecraftClient.getInstance());
        InventoryOverlay.renderInventoryStacks(type, simpleInventory, x + slotOffsetX, y + slotOffsetY, MAX_TRADE_OFFER_SIZE, 0, MAX_TRADE_OFFER_SIZE, mc);
        fi.dy.masa.malilib.render.RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

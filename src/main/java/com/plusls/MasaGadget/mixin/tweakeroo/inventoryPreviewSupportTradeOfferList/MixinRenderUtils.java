package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.RayTraceUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = RenderUtils.class, remap = false)
public class MixinRenderUtils {

    private static final int MAX_TRADE_OFFER_SIZE = 9;

    @Inject(method = "renderInventoryOverlay", at = @At(value = "RETURN"))
    private static void renderTradeOfferList(MinecraftClient mc, CallbackInfo ci) {
        World world = WorldUtils.getBestWorld(mc);
        if (!Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_TRADE_OFFER_LIST.getBooleanValue() || world == null || mc.player == null) {
            return;
        }
        PlayerEntity cameraEntity = CameraEntity.getCamera();
        if (!FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() || cameraEntity == null) {
            cameraEntity = world.getPlayerByUuid(mc.player.getUuid());
        }
        if (cameraEntity == null) {
            cameraEntity = mc.player;
        }
        HitResult trace = RayTraceUtils.getRayTraceFromEntity(world, cameraEntity, false);
        if (trace.getType() != HitResult.Type.ENTITY) {
            return;
        }
        Entity entity = ((EntityHitResult) trace).getEntity();
        if (!(entity instanceof AbstractTraderEntity)) {
            return;
        }
        BasicInventory simpleInventory = new BasicInventory(MAX_TRADE_OFFER_SIZE);
        for (TradeOffer tradeOffer : ((AbstractTraderEntity) entity).getOffers()) {
            for(int i = 0; i < simpleInventory.getInvSize(); ++i) {
                ItemStack itemStack = simpleInventory.getInvStack(i);
                if (itemStack.isEmpty()) {
                    simpleInventory.setInvStack(i, tradeOffer.getSellItem().copy());
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

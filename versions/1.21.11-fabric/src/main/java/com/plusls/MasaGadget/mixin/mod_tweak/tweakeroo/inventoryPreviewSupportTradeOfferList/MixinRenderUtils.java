package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.mixin.accessor.AccessorAbstractVillager;
import com.plusls.MasaGadget.util.ModId;
import com.plusls.MasaGadget.util.VillagerDataUtil;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.InventoryOverlayType;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.sugar.Local;

@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {
    @Unique
    private static final int masa_gadget$maxTradeOfferSize = 9;

    @ModifyVariable(
            method = "renderInventoryOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    remap = false
            ),
            ordinal = 0
    )
    private static Container renderTradeOfferList(Container inv, @Local(argsOnly = true) GuiGraphics guiGraphics) {
        if (!Configs.inventoryPreviewSupportTradeOfferList.getBooleanValue()) {
            return inv;
        }

        Entity entity = HitResultHandler.getInstance().getHitEntity().orElse(null);

        if (!(entity instanceof Villager villager)
                || VillagerDataUtil.getVillagerProfession(villager) == VillagerProfession.NONE) {
            return inv;
        }

        MerchantOffers offers = ((AccessorAbstractVillager) villager).masa_gadget_mod$getOffers();

        if (offers == null) {
            return inv;
        }

        SimpleContainer inventory = new SimpleContainer(MixinRenderUtils.masa_gadget$maxTradeOfferSize);

        for (MerchantOffer offer : offers) {
            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack itemStack = inventory.getItem(i);

                if (itemStack.isEmpty()) {
                    inventory.setItem(i, offer.getResult().copy());
                    break;
                }
            }
        }

        int x = GuiUtils.getScaledWindowWidth() / 2 - 88;
        int y = GuiUtils.getScaledWindowHeight() / 2 - 5;
        int slotOffsetX = 8;
        int slotOffsetY = 8;
        GuiContext guiContext = GuiContext.fromGuiGraphics(guiGraphics);

        InventoryOverlay.renderInventoryBackground(
                guiContext,
                InventoryOverlayType.GENERIC,
                x,
                y,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize
        );
        InventoryOverlay.renderInventoryStacks(
                guiContext,
                InventoryOverlayType.GENERIC,
                inventory,
                x + slotOffsetX,
                y + slotOffsetY,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize,
                0,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize
        );
        return inv;
    }
}

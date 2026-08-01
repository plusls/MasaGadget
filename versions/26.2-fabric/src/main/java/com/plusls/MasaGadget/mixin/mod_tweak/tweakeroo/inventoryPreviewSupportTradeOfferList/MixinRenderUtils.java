package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.mixin.accessor.AccessorAbstractVillager;
import com.plusls.MasaGadget.util.ModId;
import com.plusls.MasaGadget.util.VillagerDataUtil;
import fi.dy.masa.malilib.render.InventoryOverlayContext;
import fi.dy.masa.malilib.render.InventoryOverlayType;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.tweakeroo.renderer.InventoryOverlayHandler;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc1.20: subproject 1.16.5 (main project)</li>
 * <li>mc1.21 ~ mc1.21.10: subproject 1.21.1 [dummy]</li>
 * <li>mc1.21.11: subproject 1.21.11</li>
 * <li>mc26.1.2: subproject 26.1.2</li>
 * <li>mc26.2+: subproject 26.2        &lt;--------</li>
 */
// CHECKSTYLE.ON: JavadocStyle
@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = InventoryOverlayHandler.class, remap = false)
public abstract class MixinRenderUtils {
    @Unique
    private static final int masa_gadget$maxTradeOfferSize = 9;

    @Inject(method = "getTargetInventoryFromEntity", at = @At("HEAD"), cancellable = true)
    private void masa_gadget$addTradeOfferList(
            Entity entity,
            CompoundData data,
            CallbackInfoReturnable<InventoryOverlayContext> cir
    ) {
        if (!Configs.inventoryPreviewSupportTradeOfferList.getBooleanValue()
                || !(entity instanceof Villager villager)
                || VillagerDataUtil.getVillagerProfession(villager) == VillagerProfession.NONE) {
            return;
        }

        MerchantOffers offers = ((AccessorAbstractVillager) villager).masa_gadget_mod$getOffers();

        if (offers == null) {
            return;
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

        InventoryOverlayHandler handler = (InventoryOverlayHandler) (Object) this;
        cir.setReturnValue(new InventoryOverlayContext(
                InventoryOverlayType.GENERIC,
                inventory,
                null,
                villager,
                data,
                handler.getRefreshHandler()
        ));
    }
}

package com.plusls.MasaGadget.mixin.accessor;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractVillager.class)
public interface AccessorAbstractVillager {
    @Accessor("offers")
    void masa_gadget_mod$setOffers(MerchantOffers offers);
}

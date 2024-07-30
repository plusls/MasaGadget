package com.plusls.MasaGadget.mixin;

import com.plusls.MasaGadget.api.fake.AbstractVillagerAccessor;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//#if MC > 12006
//$$ import org.jetbrains.annotations.Nullable;
//#endif

@Mixin(AbstractVillager.class)
public abstract class MixinAbstractVillager implements AbstractVillagerAccessor {
    //#if MC > 12006
    //$$ @Shadow
    //$$ @Nullable
    //$$ protected MerchantOffers offers;
    //$$ @Shadow
    //$$ protected abstract void updateTrades();
    //#else
    @Shadow
    public abstract MerchantOffers getOffers();
    //#endif

    @Override
    public MerchantOffers masa_gadget$safeGetOffers() {
        //#if MC > 12006
        //$$ if (this.offers == null) {
        //$$     this.offers = new MerchantOffers();
        //$$     this.updateTrades();
        //$$ }
        //$$
        //$$ return this.offers;
        //#else
        return this.getOffers();
        //#endif
    }
}

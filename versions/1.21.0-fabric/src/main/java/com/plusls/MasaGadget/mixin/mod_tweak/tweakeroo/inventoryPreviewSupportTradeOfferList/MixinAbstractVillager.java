package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.plusls.MasaGadget.game.Configs;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractVillager.class)
public class MixinAbstractVillager {
    @WrapOperation(
            method = "getOffers",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/Level;isClientSide:Z"
            )
    )
    private boolean forgiveInvoke(Level instance, @NotNull Operation<Boolean> original) {
        return original.call(instance) && !Configs.inventoryPreviewSupportTradeOfferList.getBooleanValue();
    }
}

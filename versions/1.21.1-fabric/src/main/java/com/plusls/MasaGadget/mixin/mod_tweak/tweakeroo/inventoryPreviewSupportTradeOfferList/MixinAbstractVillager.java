package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.plusls.MasaGadget.game.Configs;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc1.20.6: subproject 1.16.5 [dummy] (main project)</li>
 * <li>mc1.21+          : subproject 1.21.1        &lt;--------</li>
 */
// CHECKSTYLE.ON: JavadocStyle
@Mixin(AbstractVillager.class)
public abstract class MixinAbstractVillager {
    @Unique
    private static boolean masa_gadget_mod$shouldForgeInvoke() {
        return Configs.inventoryPreviewSupportTradeOfferList.getBooleanValue()
                || Configs.renderNextRestockTime.getBooleanValue()
                || Configs.renderTradeEnchantedBook.getBooleanValue()
                || Configs.renderZombieVillagerConvertTime.getBooleanValue();
    }

    @WrapOperation(
            method = "getOffers",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/Level;isClientSide:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean forgiveInvoke(Level instance, @NotNull Operation<Boolean> original) {
        if (MixinAbstractVillager.masa_gadget_mod$shouldForgeInvoke()) {
            return false;
        }

        return original.call(instance);
    }
}

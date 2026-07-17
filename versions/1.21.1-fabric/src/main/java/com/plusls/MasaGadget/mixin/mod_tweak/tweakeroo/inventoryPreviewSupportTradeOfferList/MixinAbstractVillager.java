package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.plusls.MasaGadget.game.Configs;

// CHECKSTYLE.OFF: ImportOrder
//#if MC < 1.21.11
import org.jetbrains.annotations.NotNull;
//#endif

//#if MC < 1.21.10
import org.objectweb.asm.Opcodes;
//#endif
// CHECKSTYLE.ON: ImportOrder

import net.minecraft.world.entity.npc.AbstractVillager;

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 1.21.11
//$$ import net.minecraft.server.level.ServerLevel;
//#else
import net.minecraft.world.level.Level;
//#endif
// CHECKSTYLE.ON: ImportOrder

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 1.21.11
//$$ import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.expression.Definition;
//$$ import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.expression.Expression;
//$$ import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//#else
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//#endif
// CHECKSTYLE.ON: ImportOrder

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

    //#if MC >= 1.21.11
    //$$ @SuppressWarnings("MixinAnnotationTarget")
    //$$ // @Definition(id = "level", local = @Local(type = Level.class, name = "level"))
    //$$ @Definition(id = "ServerLevel", type = ServerLevel.class)
    //$$ @Expression("? instanceof ServerLevel")
    //$$ @ModifyExpressionValue(method = "getOffers", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    //$$ private boolean forgiveInvoke(final boolean original /*, @Local(name = "level") final Level level */) {
    //$$     if (MixinAbstractVillager.masa_gadget_mod$shouldForgeInvoke()) {
    //$$         return true;
    //$$     }
    //$$
    //$$     return original;
    //$$ }
    //#else
    @WrapOperation(
            method = "getOffers",
            at = @At(
                    //#if MC >= 1.21.10
                    //$$ value = "INVOKE",
                    //$$ target = "Lnet/minecraft/world/level/Level;isClientSide()Z"
                    //#else
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/Level;isClientSide:Z",
                    opcode = Opcodes.GETFIELD
                    //#endif
            )
    )
    private boolean forgiveInvoke(Level instance, @NotNull Operation<Boolean> original) {
        if (MixinAbstractVillager.masa_gadget_mod$shouldForgeInvoke()) {
            return false;
        }

        return original.call(instance);
    }
    //#endif
}

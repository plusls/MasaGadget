package com.plusls.MasaGadget.mixin.tweakeroo.handRestockCrafting;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.tweakeroo.handRestockCrafting.RestockUtil;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = {@Dependency(ModInfo.TWEAKEROO_MOD_ID), @Dependency(ModInfo.ITEMSCROLLER_MOD_ID)})
@Mixin(InventoryUtils.class)
public class MixinInventoryUtils {
    @Inject(method = "restockNewStackToHand", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void restockOnFailed(Player player, InteractionHand hand, ItemStack itemStack, boolean allowHotbar, CallbackInfo ci, int slotWithItem) {
        if (slotWithItem == -1 && Configs.restockWithCrafting) {
            RestockUtil.tryCraftingRestocking(player, hand, itemStack);
        }
    }

    @Inject(method = "preRestockHand", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void PreRestockOnFailed(Player player, InteractionHand hand, boolean stackSlot, CallbackInfo ci, ItemStack stackHand, int threshold) {
        if (Configs.restockWithCrafting &&
                FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() &&
                fi.dy.masa.tweakeroo.config.Configs.Generic.HAND_RESTOCK_PRE.getBooleanValue() &&
                !stackHand.isEmpty() &&
                stackHand.getCount() <= threshold && stackHand.getMaxStackSize() > threshold &&
                //#if MC > 11605
                PlacementTweaks.canUseItemWithRestriction(PlacementTweaks.HAND_RESTOCK_RESTRICTION, stackHand) &&
                //#endif
                player.containerMenu == player.inventoryMenu &&
                //#if MC > 11605
                player.containerMenu.getCarried().isEmpty()
            //#else
            //$$ player.getInventory().getCarried().isEmpty()
            //#endif
        ) {
            RestockUtil.tryCraftingRestocking(player, hand, stackHand);
        }
    }
}

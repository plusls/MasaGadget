package com.plusls.MasaGadget.mixin.tweakeroo.handRestockCrafting;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.tweakeroo.handRestockCrafting.RestockUtil;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Dependencies(dependencyList = {@Dependency(modId = ModInfo.TWEAKEROO_MOD_ID, version = "*"), @Dependency(modId = ModInfo.ITEMSCROLLER_MOD_ID, version = "*")})
@Mixin(InventoryUtils.class)
public class MixinInventoryUtils {
    @Inject(method = "restockNewStackToHand", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void restockOnFailed(PlayerEntity player, Hand hand, ItemStack itemStack, boolean allowHotbar, CallbackInfo ci, int slotWithItem) {
        if (slotWithItem == -1 && com.plusls.MasaGadget.config.Configs.Tweakeroo.RESTOCK_WITH_CRAFTING.getBooleanValue()) {
            RestockUtil.tryCraftingRestocking(player, hand, itemStack);
        }
    }

    @Inject(method = "preRestockHand", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void PreRestockOnFailed(PlayerEntity player, Hand hand, boolean stackSlot, CallbackInfo ci, ItemStack stackHand, int threshold) {
        if (com.plusls.MasaGadget.config.Configs.Tweakeroo.RESTOCK_WITH_CRAFTING.getBooleanValue() &&
                FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() &&
                fi.dy.masa.tweakeroo.config.Configs.Generic.HAND_RESTOCK_PRE.getBooleanValue() &&
                !stackHand.isEmpty() &&
                stackHand.getCount() <= threshold && stackHand.getMaxCount() > threshold &&
                player.currentScreenHandler == player.playerScreenHandler &&
                player.inventory.getCursorStack().isEmpty()) {
            RestockUtil.tryCraftingRestocking(player, hand, stackHand);
        }
    }
}

package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.handRestockCrafting;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.util.ModId;
import com.plusls.MasaGadget.util.RestockUtil;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = {
        @Dependency(ModId.tweakeroo),
        @Dependency(ModId.itemscroller)
})
@Mixin(value = InventoryUtils.class, remap = false)
public class MixinInventoryUtils {
    @Inject(
            method = "restockNewStackToHand",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void restockOnFailed(Player player, InteractionHand hand, ItemStack itemStack,
                                        boolean allowHotbar, CallbackInfo ci, int slotWithItem) {
        if (slotWithItem == -1 && Configs.restockWithCrafting.getBooleanValue()) {
            RestockUtil.tryCraftingRestocking(player, hand, itemStack);
        }
    }

    @Inject(
            method = "preRestockHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;getInstance()Lnet/minecraft/client/Minecraft;",
                    remap = true
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void PreRestockOnFailed(Player player, InteractionHand hand,
                                           boolean stackSlot, CallbackInfo ci, ItemStack stackHand) {
        if (Configs.restockWithCrafting.getBooleanValue()) {
            RestockUtil.tryCraftingRestocking(player, hand, stackHand);
        }
    }
}

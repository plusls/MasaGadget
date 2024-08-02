package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportSelect;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.mod_tweak.tweakeroo.inventoryPreviewSupportSelect.InventoryOverlayRenderHandler;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif

@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = InventoryOverlay.class, remap = false)
public class MixinInventoryOverlay {
    @Inject(
            //#if MC > 12006
            // Inject the final call.
            //$$ method = "renderStackAt(Lnet/minecraft/world/item/ItemStack;FFFLnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            //$$ remap = true,
            //#else
            method = "renderStackAt",
            //#endif
            at = @At("RETURN")
    )
    private static void addStackToolTip(
            ItemStack stack,
            float x,
            float y,
            float scale,
            Minecraft mc,
            //#if MC > 11904
            //$$ GuiGraphics gui,
            //#endif
            //#if MC > 12006
            //$$ double mouseX,
            //$$ double mouseY,
            //#endif
            CallbackInfo ci
    ) {
        if (Configs.inventoryPreviewSupportSelect.getBooleanValue()) {
            InventoryOverlayRenderHandler.getInstance().updateState((int) x, (int) y, stack);
        }
    }
}

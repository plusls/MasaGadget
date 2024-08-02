package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportPlayer;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.player.PlayerCompat;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {
    //#if MC > 11904
    //$$ @Unique
    //$$ private static GuiGraphics masa_gadget$gui;
    //$$
    //$$ @Inject(method = "renderInventoryOverlay", at = @At("HEAD"))
    //$$ private static void intercept(Minecraft mc, GuiGraphics gui, CallbackInfo ci) {
    //$$     MixinRenderUtils.masa_gadget$gui = gui;
    //$$ }
    //#endif

    @ModifyVariable(
            method = "renderInventoryOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    remap = false
            )
    )
    private static Container modifyInv(Container inv) {
        Container ret = inv;
        Entity traceEntity = HitResultHandler.getInstance().getHitEntity().orElse(null);

        if (Configs.inventoryPreviewSupportPlayer.getBooleanValue() &&
                ret == null && traceEntity instanceof Player) {
            Player player = (Player) traceEntity;
            PlayerCompat playerCompat = PlayerCompat.of(player);
            ret = playerCompat.getInventory();
            int x = GuiUtils.getScaledWindowWidth() / 2 - 88;
            int y = GuiUtils.getScaledWindowHeight() / 2 + 10;
            int slotOffsetX = 8;
            int slotOffsetY = 8;
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.GENERIC;
            DyeColor dye = DyeColor.GRAY;
            //#if MC > 12006
            //$$ float[] colors = fi.dy.masa.malilib.render.RenderUtils.getColorComponents(dye.getTextureDiffuseColor());
            //#else
            float[] colors = dye.getTextureDiffuseColors();
            //#endif
            fi.dy.masa.malilib.render.RenderUtils.color(colors[0], colors[1], colors[2], 1.0F);
            InventoryOverlay.renderInventoryBackground(type, x, y, 9, 27, Minecraft.getInstance());
            InventoryOverlay.renderInventoryStacks(
                    type,
                    player.getEnderChestInventory(),
                    x + slotOffsetX,
                    y + slotOffsetY,
                    9,
                    0,
                    27,
                    Minecraft.getInstance()
                    //#if MC > 11904
                    //$$ , masa_gadget$gui
                    //#endif
            );
            fi.dy.masa.malilib.render.RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        return ret;
    }
}

package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.util.ModId;
import com.plusls.MasaGadget.util.VillagerDataUtil;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.InventoryOverlay.InventoryRenderType;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

//#if MC > 11904
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.sugar.Local;
//#endif

@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = RenderUtils.class, remap = false)
public class MixinRenderUtils {
    @Unique
    private static final int masa_gadget$maxTradeOfferSize = 9;

    @ModifyVariable(
            method = "renderInventoryOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    remap = false
            ),
            ordinal = 0
    )
    private static Container renderTradeOfferList(
            Container inv
            //#if MC > 11904
            //$$ , @Local(argsOnly = true) GuiGraphics guiGraphics
            //#endif
    ) {
        if (!Configs.inventoryPreviewSupportTradeOfferList.getBooleanValue()) {
            return inv;
        }

        Entity entity = HitResultHandler.getInstance().getHitEntity().orElse(null);

        if (!(entity instanceof Villager)) {
            return inv;
        }

        Villager villager = (Villager) entity;

        if (villager instanceof Villager &&
                VillagerDataUtil.getVillagerProfession(villager) == VillagerProfession.NONE) {
            return inv;
        }

        SimpleContainer simpleInventory = new SimpleContainer(MixinRenderUtils.masa_gadget$maxTradeOfferSize);

        for (MerchantOffer tradeOffer : villager.getOffers()) {
            for (int i = 0; i < simpleInventory.getContainerSize(); ++i) {
                ItemStack itemStack = simpleInventory.getItem(i);

                if (itemStack.isEmpty()) {
                    simpleInventory.setItem(i, tradeOffer.getResult().copy());
                    break;
                }
            }
        }

        int x = GuiUtils.getScaledWindowWidth() / 2 - 88;
        int y = GuiUtils.getScaledWindowHeight() / 2 - 5;
        int slotOffsetX = 8;
        int slotOffsetY = 8;
        InventoryRenderType type = InventoryRenderType.GENERIC;
        DyeColor dye = DyeColor.GREEN;
        //#if MC < 12106
        //#if MC > 12006
        //$$ float[] colors = fi.dy.masa.malilib.render.RenderUtils.getColorComponents(dye.getTextureDiffuseColor());
        //#else
        float[] colors = dye.getTextureDiffuseColors();
        //#endif
        fi.dy.masa.malilib.render.RenderUtils.color(colors[0], colors[1], colors[2], 1.0F);
        //#endif
        InventoryOverlay.renderInventoryBackground(
                //#if MC >= 12106
                //$$ guiGraphics,
                //#endif
                type,
                x,
                y,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize,
                //#if MC >= 12106
                //$$ dye.getTextureDiffuseColor(),
                //#endif
                Minecraft.getInstance()
                //#if 12106 > MC && MC > 12104
                //$$ , guiGraphics
                //#endif
        );
        InventoryOverlay.renderInventoryStacks(
                //#if MC >= 12106
                //$$ guiGraphics,
                //#endif
                type,
                simpleInventory,
                x + slotOffsetX,
                y + slotOffsetY,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize,
                0,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize,
                Minecraft.getInstance()
                //#if 12106 > MC && MC > 11904
                //$$ , guiGraphics
                //#endif
        );
        //#if MC < 12106
        fi.dy.masa.malilib.render.RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
        //#endif
        return inv;
    }
}

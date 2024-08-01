package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportTradeOfferList;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
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
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = RenderUtils.class, remap = false)
public class MixinRenderUtils {
    //#if MC > 11904
    //$$ @Unique
    //$$ private static GuiGraphics masa_gadget$gui;
    //$$
    //$$ @Inject(method = "renderInventoryOverlay", at = @At("HEAD"))
    //$$ private static void intercept(Minecraft mc, GuiGraphics gui, CallbackInfo ci) {
    //$$     MixinRenderUtils.masa_gadget$gui = gui;
    //$$ }
    //#endif

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
    private static Container renderTradeOfferList(Container inv) {
        if (!Configs.inventoryPreviewSupportTradeOfferList.getBooleanValue()) {
            return inv;
        }

        Entity entity = HitResultHandler.getInstance().getHitEntity().orElse(null);

        if (!(entity instanceof AbstractVillager)) {
            return inv;
        }

        AbstractVillager abstractVillager = (AbstractVillager) entity;

        if (abstractVillager instanceof Villager &&
                ((Villager) abstractVillager).getVillagerData().getProfession() == VillagerProfession.NONE) {
            return inv;
        }

        SimpleContainer simpleInventory = new SimpleContainer(MixinRenderUtils.masa_gadget$maxTradeOfferSize);

        for (MerchantOffer tradeOffer : abstractVillager.getOffers()) {
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
        InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.GENERIC;
        DyeColor dye = DyeColor.GREEN;
        //#if MC > 12006
        //$$ float[] colors = fi.dy.masa.malilib.render.RenderUtils.getColorComponents(dye.getTextureDiffuseColor());
        //#else
        float[] colors = dye.getTextureDiffuseColors();
        //#endif

        fi.dy.masa.malilib.render.RenderUtils.color(colors[0], colors[1], colors[2], 1.0F);
        InventoryOverlay.renderInventoryBackground(type, x, y, MixinRenderUtils.masa_gadget$maxTradeOfferSize,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize, Minecraft.getInstance());
        InventoryOverlay.renderInventoryStacks(
                type,
                simpleInventory,
                x + slotOffsetX,
                y + slotOffsetY,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize,
                0,
                MixinRenderUtils.masa_gadget$maxTradeOfferSize,
                Minecraft.getInstance()
                //#if MC > 11904
                //$$ , masa_gadget$gui
                //#endif
        );
        fi.dy.masa.malilib.render.RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
        return inv;
    }
}

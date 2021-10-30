package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportShulkerBoxItemEntity;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import com.plusls.MasaGadget.tweakeroo.TraceUtil;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {


    @ModifyVariable(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    ordinal = 0, remap = false), ordinal = 0)
    private static Inventory modifyInv(Inventory inv) {
        Inventory ret = inv;
        Entity traceEntity = TraceUtil.getTraceEntity();

        if (Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_SHULKER_BOX_ITEM_ENTITY.getBooleanValue() && ret == null &&
                traceEntity instanceof ItemEntity) {
            ItemStack itemStack = ((ItemEntity) traceEntity).getStack();
            Item item = itemStack.getItem();
            CompoundTag invNbt = itemStack.getSubTag("BlockEntityTag");
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock) {
                ret = new BasicInventory(27);
                if (invNbt != null) {
                    Inventories.fromTag(invNbt, stacks);
                }
                for (int i = 0; i < 27; ++i) {
                    ret.setInvStack(i, stacks.get(i));
                }

            }
        }
        return ret;
    }
}

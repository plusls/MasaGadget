package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportShulkerBoxItemEntity;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.HitResultUtil;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {

    @ModifyVariable(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    ordinal = 0, remap = false), ordinal = 0)
    private static Container modifyInv(Container inv) {
        Container ret = inv;
        Entity traceEntity = HitResultUtil.getHitEntity();

        if (Configs.inventoryPreviewSupportShulkerBoxItemEntity && ret == null &&
                traceEntity instanceof ItemEntity) {
            ItemStack itemStack = ((ItemEntity) traceEntity).getItem();
            Item item = itemStack.getItem();
            CompoundTag invNbt = itemStack.getTagElement("BlockEntityTag");
            NonNullList<ItemStack> stacks = NonNullList.withSize(27, ItemStack.EMPTY);
            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock) {
                ret = new SimpleContainer(27);
                if (invNbt != null) {
                    ContainerHelper.loadAllItems(invNbt, stacks);
                }
                for (int i = 0; i < 27; ++i) {
                    ret.setItem(i, stacks.get(i));
                }

            }
        }
        return ret;
    }
}

package io.github.plusls.MasaGadget.mixin.server.item;

import io.github.plusls.MasaGadget.MasaGadgetMod;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    public void getMaxStackSizeStackSensitive(CallbackInfoReturnable<Integer> ci) {
        // support shulkerbox stack
        Item item = this.getItem();
        // MasaGadgetMod.LOGGER.info("ItemStack: {}", this);
        if (this.getItem() instanceof BlockItem &&
                ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock &&
                !shulkerBoxHasItems((ItemStack) (Object) this)) {
            //MasaGadgetMod.LOGGER.info("Modify ret!");
            ci.setReturnValue(64);
            ci.cancel();
        }
    }

    private static boolean shulkerBoxHasItems(ItemStack stackShulkerBox) {
        // MasaGadgetMod.LOGGER.info("Try to calc shulkerBoxHasItems: {}", stackShulkerBox);
        CompoundTag nbt = stackShulkerBox.getTag();
        if (nbt != null && nbt.contains("BlockEntityTag", 10)) {
            CompoundTag tag = nbt.getCompound("BlockEntityTag");
            if (tag.contains("Items", 9)) {
                ListTag tagList = tag.getList("Items", 10);
                return tagList.size() > 0;
            }
        }
        return false;
    }
}

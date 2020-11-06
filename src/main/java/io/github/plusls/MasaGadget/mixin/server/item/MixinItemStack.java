package io.github.plusls.MasaGadget.mixin.server.item;

import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Shadow
    public abstract Item getItem();

    private static final int SHULKERBOX_MAX_STACK_AMOUNT = 64;

    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    public void getMaxStackSizeStackSensitive(CallbackInfoReturnable<Integer> ci) {
        // support shulkerbox stack
        Item item = this.getItem();
        if (this.getItem() instanceof BlockItem &&
                ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock &&
                !shulkerBoxHasItems((ItemStack) (Object) this)) {
            // new Exception().printStackTrace(System.out);
            // 效率很低 但是 it works
            // 通过栈帧判断调用者来过滤返回值
            StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
            if (checkStackTrace(steArray, InventoryChangedCriterion.class.getName(), "method_8950", new int[]{3}) || // method_8950 -> InventoryChangedCriterion.trigger
                    checkStackTrace(steArray, PlayerInventory.class.getName(), "method_7393", new int[]{3, 4}) || // method_7393-> PlayerInventory.canStackAddMore
                    checkStackTrace(steArray, PlayerInventory.class.getName(), "method_7385", new int[]{3, 4}) || // method_7385 -> PlayerInventory.addStack
                    checkStackTrace(steArray, ScreenHandler.class.getName(), "method_7616", new int[]{3, 4}) || // method_7616 -> ScreenHandler.insertItem
                    checkStackTrace(steArray, ScreenHandler.class.getName(), "method_30010", new int[]{3}) // method_30010 -> ScreenHandler.method_30010
            ) {
                ci.setReturnValue(SHULKERBOX_MAX_STACK_AMOUNT);
                return;
            }
        }
    }

    private static boolean checkStackTrace(StackTraceElement[] steArray, String className, String methodName, int[] indexArray) {
        for (int i : indexArray) {
            if (steArray.length < i + 1) {
                return false;
            }
            if (steArray[i].getClassName().equals(className) && steArray[i].getMethodName().equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean shulkerBoxHasItems(ItemStack stackShulkerBox) {
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

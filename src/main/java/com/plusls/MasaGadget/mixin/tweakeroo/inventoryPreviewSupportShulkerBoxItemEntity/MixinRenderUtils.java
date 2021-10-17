package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportShulkerBoxItemEntity;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Dependencies(dependencyList = @Dependency(modId = MasaGadgetMixinPlugin.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {

    @Unique
    static private Entity inventoryPreviewSupportShulkerBoxItemEntityTraceEntity = null;

    @Inject(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;",
                    ordinal = 0, remap = true), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void getTraceEntity(MinecraftClient mc, MatrixStack matrixStack, CallbackInfo ci,
                                       World world, PlayerEntity player, HitResult trace, Inventory inv, ShulkerBoxBlock block,
                                       LivingEntity entityLivingBase, Entity entity) {
        inventoryPreviewSupportShulkerBoxItemEntityTraceEntity = entity;
    }

    @Surrogate
    private static void getTraceEntity(MinecraftClient mc, MatrixStack matrixStack, CallbackInfo ci,
                                       World world, Entity cameraEntity, HitResult trace, Inventory inv, ShulkerBoxBlock block,
                                       LivingEntity entityLivingBase, Entity entity) {
        inventoryPreviewSupportShulkerBoxItemEntityTraceEntity = entity;
    }

    @ModifyVariable(method = "renderInventoryOverlay",
            at = @At(value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    ordinal = 0, remap = false), ordinal = 0)
    private static Inventory modifyInv(Inventory inv) {
        Inventory ret = inv;
        if (Configs.Tweakeroo.INVENTORY_PREVIEW_SUPPORT_SHULKER_BOX_ITEM_ENTITY.getBooleanValue() && ret == null &&
                inventoryPreviewSupportShulkerBoxItemEntityTraceEntity instanceof ItemEntity) {
            ItemStack itemStack = ((ItemEntity) inventoryPreviewSupportShulkerBoxItemEntityTraceEntity).getStack();
            Item item = itemStack.getItem();
            NbtCompound invNbt = itemStack.getSubTag("BlockEntityTag");
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock) {
                ret = new SimpleInventory(27);
                if (invNbt != null) {
                    Inventories.readNbt(invNbt, stacks);
                }
                for (int i = 0; i < 27; ++i) {
                    ret.setStack(i, stacks.get(i));
                }

            }
        }
        inventoryPreviewSupportShulkerBoxItemEntityTraceEntity = null;
        return ret;
    }
}

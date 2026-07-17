package com.plusls.MasaGadget.mixin.mod_tweak.tweakeroo.inventoryPreviewSupportShulkerBoxItemEntity;

import com.plusls.MasaGadget.game.Configs;
import com.plusls.MasaGadget.impl.generic.HitResultHandler;
import com.plusls.MasaGadget.util.ModId;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 12106
//$$ import org.slf4j.Logger;
//#endif
// CHECKSTYLE.ON: ImportOrder

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

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 1.21.10
//$$ import net.minecraft.world.item.component.TypedEntityData;
//$$ import net.minecraft.world.level.block.entity.BlockEntityType;
//#endif

//#if 1.21.10 > MC && MC > 1.20.4
//$$ import net.minecraft.world.item.component.CustomData;
//#endif

//#if MC >= 12106
//$$ import com.mojang.logging.LogUtils;
//$$ import net.minecraft.world.level.storage.TagValueInput;
//$$ import net.minecraft.world.level.storage.ValueInput;
//$$ import net.minecraft.util.ProblemReporter;
//#endif

//#if MC > 12004
//$$ import top.hendrixshen.magiclib.api.compat.minecraft.client.MinecraftCompat;
//$$ import net.minecraft.core.component.DataComponents;
//#endif
// CHECKSTYLE.ON: ImportOrder

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// CHECKSTYLE.OFF: ImportOrder
//#if MC >= 12106
//$$ import org.spongepowered.asm.mixin.Unique;
//#endif
// CHECKSTYLE.ON: ImportOrder

// CHECKSTYLE.OFF: JavadocStyle
/**
 * <li>mc1.14 ~ mc1.20: subproject 1.16.5 (main project)        &lt;--------</li>
 * <li>mc1.21+        : subproject 1.21.1 [dummy]</li>
 */
// CHECKSTYLE.ON: JavadocStyle
@Dependencies(require = @Dependency(ModId.tweakeroo))
@Mixin(value = RenderUtils.class, remap = false)
public abstract class MixinRenderUtils {
    //#if MC >= 12106
    //$$ @Unique
    //$$ private static final Logger masa_gadget$logger = LogUtils.getLogger();
    //#endif

    @ModifyVariable(
            method = "renderInventoryOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/malilib/util/GuiUtils;getScaledWindowWidth()I",
                    remap = false
            ),
            ordinal = 0
    )
    private static Container modifyInv(Container inv) {
        Container ret = inv;
        Entity traceEntity = HitResultHandler.getInstance().getHitEntity().orElse(null);

        if (Configs.inventoryPreviewSupportShulkerBoxItemEntity.getBooleanValue()
                && ret == null
                && traceEntity instanceof ItemEntity
        ) {
            ItemStack itemStack = ((ItemEntity) traceEntity).getItem();
            Item item = itemStack.getItem();
            //#if MC > 12004
            //#if MC >= 1.21.10
            //$$ TypedEntityData<BlockEntityType<?>> invData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
            //#else
            //$$ CustomData invData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
            //#endif
            //$$
            //$$ if (invData == null) {
            //$$     return null;
            //$$ }
            //$$
            //#if MC >= 1.21.10
            //$$ CompoundTag invNbt = invData.copyTagWithoutId();
            //#else
            //$$ CompoundTag invNbt = invData.copyTag();
            //#endif
            //#else
            CompoundTag invNbt = itemStack.getTagElement("BlockEntityTag");
            //#endif

            NonNullList<ItemStack> stacks = NonNullList.withSize(27, ItemStack.EMPTY);

            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock) {
                ret = new SimpleContainer(27);

                //#if MC >= 12106
                //$$ try (ProblemReporter.ScopedCollector collector = new ProblemReporter.ScopedCollector(MixinRenderUtils.masa_gadget$logger)) {
                //$$     ValueInput input = TagValueInput.create(collector, MinecraftCompat.getInstance().getMainCamera().getEntity().registryAccess(), invNbt);
                //$$     ContainerHelper.loadAllItems(input, stacks);
                //$$ }
                //#else
                if (invNbt != null) {
                    ContainerHelper.loadAllItems(
                            // CHECKSTYLE.OFF: NoWhitespaceBefore
                            // CHECKSTYLE.OFF: SeparatorWrap
                            invNbt,
                            stacks
                            //#if MC > 12004
                            //$$ , MinecraftCompat.getInstance().getMainCamera().getEntity().registryAccess()
                            //#endif
                            // CHECKSTYLE.ON: SeparatorWrap
                            // CHECKSTYLE.ON: NoWhitespaceBefore
                    );
                }
                //#endif

                for (int i = 0; i < 27; i++) {
                    ret.setItem(i, stacks.get(i));
                }
            }
        }

        return ret;
    }
}

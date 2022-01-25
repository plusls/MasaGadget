package com.plusls.MasaGadget.tweakeroo.handRestockCrafting;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.itemscroller.recipes.RecipePattern;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.stream.Collectors;

public class RestockUtil {
    private static List<RecipePattern> recipes;

    public static void updateRecipes() {
        RecipeStorage storage = RecipeStorage.getInstance();
        try {
            RestockUtil.recipes = Configs.Tweakeroo.RESTOCK_WITH_CRAFTING_RECIPES.getStrings().stream()
                    .mapToInt(Integer::valueOf)
                    .mapToObj(storage::getRecipe)
                    .filter(recipe -> recipe.getRecipeLength() == 4)
                    .collect(Collectors.toList());
        } catch (NumberFormatException exception) {
            ModInfo.LOGGER.error(exception);
        }
    }

    public static void tryCraftingRestocking(PlayerEntity player, Hand hand, ItemStack itemStack) {
        if (player.isCreative()) {
            return;
        }
        updateRecipes();
        // TODO: Lazy update (on itemscoller's RecipeStorage updated or config changed)
        MinecraftClient mc = MinecraftClient.getInstance();
        HandledScreen<? extends ScreenHandler> gui = new InventoryScreen(player);
        gui.init(MinecraftClient.getInstance(), 0, 0);
        for (RecipePattern recipe : RestockUtil.recipes) {
            if (ItemStack.canCombine(recipe.getResult(), itemStack)) {
                InventoryUtils.tryMoveItemsToFirstCraftingGrid(recipe, gui, false);
                mc.interactionManager.clickSlot(player.playerScreenHandler.syncId, 0, 0, SlotActionType.PICKUP, player);
                if (player.playerScreenHandler.getCursorStack().isItemEqual(itemStack)) {
                    mc.interactionManager.clickSlot(player.playerScreenHandler.syncId,
                            hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot + 36 : 45,
                            0, SlotActionType.PICKUP, player);
                    return;
                } else {
                    Slot slot = CraftingHandler.getFirstCraftingOutputSlotForGui(gui);
                    CraftingHandler.SlotRange range = CraftingHandler.getCraftingGridSlots(gui, slot);
                    for (int s = range.getFirst(); s <= range.getLast(); s++) {
                        mc.interactionManager.clickSlot(gui.getScreenHandler().syncId, s, 0, SlotActionType.QUICK_MOVE, mc.player);
                    }
                }
            }
        }
    }
}

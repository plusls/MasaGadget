package com.plusls.MasaGadget.tweakeroo.handRestockCrafting;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.itemscroller.recipes.RecipePattern;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import top.hendrixshen.magiclib.compat.minecraft.world.item.ItemStackCompatApi;

import java.util.List;
import java.util.stream.Collectors;

public class RestockUtil {
    private static List<RecipePattern> recipes;

    public static void updateRecipes() {
        RecipeStorage storage = RecipeStorage.getInstance();
        try {
            RestockUtil.recipes = Configs.restockWithCraftingRecipes.stream()
                    .mapToInt(Integer::valueOf)
                    .mapToObj(storage::getRecipe)
                    .filter(recipe -> recipe.getRecipeLength() == 4)
                    .collect(Collectors.toList());
        } catch (NumberFormatException exception) {
            ModInfo.LOGGER.error(exception);
        }
    }

    public static void tryCraftingRestocking(Player player, InteractionHand hand, ItemStack itemStack) {
        if (player.isCreative()) {
            return;
        }
        updateRecipes();
        // TODO: Lazy update (on itemscoller's RecipeStorage updated or config changed)
        Minecraft mc = Minecraft.getInstance();
        AbstractContainerScreen<? extends AbstractContainerMenu> gui = new InventoryScreen(player);
        gui.init(Minecraft.getInstance(), 0, 0);
        for (RecipePattern recipe : RestockUtil.recipes) {
            if (ItemStackCompatApi.isSameItemSameTags(recipe.getResult(), itemStack)) {
                InventoryUtils.tryMoveItemsToFirstCraftingGrid(recipe, gui, false);
                mc.gameMode.handleInventoryMouseClick(player.inventoryMenu.containerId, 0, 0, ClickType.PICKUP, player);
                //#if MC > 11605
                if (player.inventoryMenu.getCarried()
                        //#else
                        //$$ if(player.getInventory().getCarried()
                        //#endif
                        .sameItemStackIgnoreDurability(itemStack)) {
                    mc.gameMode.handleInventoryMouseClick(player.inventoryMenu.containerId,
                            hand == InteractionHand.MAIN_HAND ? player.getInventory().selected + 36 : 45,
                            0, ClickType.PICKUP, player);
                    return;
                } else {
                    Slot slot = CraftingHandler.getFirstCraftingOutputSlotForGui(gui);
                    CraftingHandler.SlotRange range = CraftingHandler.getCraftingGridSlots(gui, slot);
                    for (int s = range.getFirst(); s <= range.getLast(); s++) {
                        mc.gameMode.handleInventoryMouseClick(gui.getMenu().containerId, s, 0, ClickType.QUICK_MOVE, mc.player);
                    }
                }
            }
        }
    }
}

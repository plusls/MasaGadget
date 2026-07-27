package com.plusls.MasaGadget.util;

import com.google.common.collect.Lists;
import com.plusls.MasaGadget.SharedConstants;
import com.plusls.MasaGadget.game.Configs;
import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.itemscroller.recipes.RecipePattern;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.player.PlayerCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.item.ItemStackCompat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

//#if MC >= 260100
//$$ import net.minecraft.world.inventory.ContainerInput;
//#else
import net.minecraft.world.inventory.ClickType;
//#endif

public class RestockUtil {
    private static final List<RecipePattern> recipes = Lists.newArrayList();

    public static void updateRecipes() {
        RecipeStorage storage = RecipeStorage.getInstance();

        try {
            RestockUtil.recipes.clear();
            RestockUtil.recipes.addAll(Configs.restockWithCraftingRecipes.getStrings().stream()
                    .mapToInt(Integer::valueOf)
                    .mapToObj(storage::getRecipe)
                    .filter(recipe -> recipe.getRecipeLength() == 4)
                    .collect(Collectors.toList()));
        } catch (NumberFormatException exception) {
            SharedConstants.getLogger().error(exception);
        }
    }

    public static void tryCraftingRestocking(Player player, InteractionHand hand, ItemStack itemStack) {
        if (player.isCreative()) {
            return;
        }

        RestockUtil.updateRecipes();
        // TODO: Lazy update (on itemscoller's RecipeStorage updated or config changed)
        Minecraft mc = Minecraft.getInstance();
        AbstractContainerScreen<? extends AbstractContainerMenu> gui = new InventoryScreen(player);
        //#if MC >= 12111
        //$$ gui.init(0, 0);
        //#else
        gui.init(mc, 0, 0); // gui.init(Minecraft.getInstance(), 0, 0);
        //#endif

        for (RecipePattern recipe : RestockUtil.recipes) {
            if (ItemStackCompat.isSameItemSameTags(recipe.getResult(), itemStack)) {
                InventoryUtils.tryMoveItemsToFirstCraftingGrid(recipe, gui, false);
                //#if MC >= 260100
                //$$ mc.gameMode.handleContainerInput(player.inventoryMenu.containerId, 0, 0, ContainerInput.PICKUP, player);
                //#else
                mc.gameMode.handleInventoryMouseClick(player.inventoryMenu.containerId, 0, 0, ClickType.PICKUP, player);
                //#endif

                if (
                        //#if MC > 11904
                        //$$ ItemStack.isSameItem(player.inventoryMenu.getCarried(), itemStack)
                        //#elseif MC > 11902
                        //$$ player.inventoryMenu.getCarried().sameItem(itemStack)
                        //#elseif MC > 11605
                        //$$ player.inventoryMenu.getCarried().sameItemStackIgnoreDurability(itemStack)
                        //#else
                        PlayerCompat.of(player).getInventory().getCarried().sameItemStackIgnoreDurability(itemStack)
                        //#endif
                ) {
                    //#if MC >= 260100
                    //$$ mc.gameMode.handleContainerInput(player.inventoryMenu.containerId,
                    //#else
                    mc.gameMode.handleInventoryMouseClick(player.inventoryMenu.containerId,
                    //#endif
                            hand == InteractionHand.MAIN_HAND ?
                            //#if MC > 12104
                            //$$ PlayerCompat.of(player).getInventory().getSelectedSlot() + 36 :
                            //#else
                            PlayerCompat.of(player).getInventory().selected + 36 :
                            //#endif
                             45,
                            0,
                            //#if MC >= 260100
                            //$$ ContainerInput.PICKUP,
                            //#else
                            ClickType.PICKUP,
                            //#endif
                            player);
                    return;
                } else {
                    Slot slot = CraftingHandler.getFirstCraftingOutputSlotForGui(gui);
                    CraftingHandler.SlotRange range = CraftingHandler.getCraftingGridSlots(gui, slot);

                    for (int s = range.getFirst(); s <= range.getLast(); s++) {
                        //#if MC >= 260100
                        //$$ mc.gameMode.handleContainerInput(gui.getMenu().containerId, s, 0, ContainerInput.QUICK_MOVE, mc.player);
                        //#else
                        mc.gameMode.handleInventoryMouseClick(gui.getMenu().containerId, s, 0, ClickType.QUICK_MOVE, mc.player);
                        //#endif
                    }
                }
            }
        }
    }
}

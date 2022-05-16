package com.plusls.MasaGadget.generic.cacheContainerMenu.cacheContainerMenu;

import com.plusls.MasaGadget.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CacheContainerMenuHandler {
    @Nullable
    public static BlockPos lastClickBlockPos = null;

    private static long lastClickTime = System.nanoTime();


    public static void setLastClickBlockPos(BlockPos blockPos) {
        lastClickBlockPos = blockPos;
        lastClickTime = System.nanoTime();
    }

    public static void updateLastClickBlockPos() {
        long currentTime = System.nanoTime();
        // 延迟超过 1 s 才打开的容器则会被忽略
        if (lastClickBlockPos == null || currentTime - lastClickTime > 1000000000 || !checkContainerMenu()) {
            clearLastClickData();
        }
    }

    public static void clearLastClickData() {
        lastClickBlockPos = null;
    }

    @Nullable
    public static Container getLastClickContainer() {
        if (lastClickBlockPos != null) {
            return MiscUtil.getContainer(Objects.requireNonNull(Minecraft.getInstance().level), lastClickBlockPos);
        }
        return null;
    }


    public static boolean checkContainerMenu() {
        Minecraft minecraft = Minecraft.getInstance();
        AbstractContainerMenu containerMenu = Objects.requireNonNull(minecraft.player).containerMenu;
        if (lastClickBlockPos == null) {
            return false;
        }
        Block block = Objects.requireNonNull(minecraft.level).getBlockState(lastClickBlockPos).getBlock();
        return (containerMenu instanceof AbstractFurnaceMenu && block instanceof AbstractFurnaceBlock) ||
                (containerMenu instanceof HopperMenu && block instanceof HopperBlock) ||
                (containerMenu instanceof ShulkerBoxMenu && block instanceof ShulkerBoxBlock) ||
                (containerMenu instanceof BrewingStandMenu && block instanceof BrewingStandBlock) ||
                (containerMenu instanceof DispenserMenu && block instanceof DispenserBlock) ||
                (containerMenu instanceof ChestMenu && (block instanceof ChestBlock || block instanceof BarrelBlock));
    }

}

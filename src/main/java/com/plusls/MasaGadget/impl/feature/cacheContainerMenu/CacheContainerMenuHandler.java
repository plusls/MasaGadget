package com.plusls.MasaGadget.impl.feature.cacheContainerMenu;

import com.plusls.MasaGadget.util.MiscUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheContainerMenuHandler {
    @Getter
    private static final CacheContainerMenuHandler instance = new CacheContainerMenuHandler();

    @Nullable
    @Getter
    private BlockPos lastClickBlockPos = null;
    private long lastClickTime = System.nanoTime();

    public void setLastClickBlockPos(BlockPos blockPos) {
        this.lastClickBlockPos = blockPos;
        this.lastClickTime = System.nanoTime();
    }

    public void checkLastClickBlockPos() {
        long currentTime = System.nanoTime();

        if (lastClickBlockPos == null ||
                // Containers opened with a delay of more than 1s are ignored.
                currentTime - this.lastClickTime > 1E9 ||
                !this.isAvailableMenu()) {
            this.clearLastClickData();
        }
    }

    public void clearLastClickData() {
        this.lastClickBlockPos = null;
    }

    @Nullable
    public Container getLastClickContainer() {
        if (this.lastClickBlockPos != null) {
            return MiscUtil.getContainer(Objects.requireNonNull(Minecraft.getInstance().level), this.lastClickBlockPos);
        }

        return null;
    }

    public boolean isAvailableMenu() {
        Minecraft minecraft = Minecraft.getInstance();
        AbstractContainerMenu containerMenu = Objects.requireNonNull(minecraft.player).containerMenu;

        if (this.lastClickBlockPos == null) {
            return false;
        }

        Block block = Objects.requireNonNull(minecraft.level).getBlockState(this.lastClickBlockPos).getBlock();
        return (containerMenu instanceof AbstractFurnaceMenu && block instanceof AbstractFurnaceBlock) ||
                (containerMenu instanceof HopperMenu && block instanceof HopperBlock) ||
                (containerMenu instanceof ShulkerBoxMenu && block instanceof ShulkerBoxBlock) ||
                (containerMenu instanceof BrewingStandMenu && block instanceof BrewingStandBlock) ||
                (containerMenu instanceof DispenserMenu && block instanceof DispenserBlock) ||
                (containerMenu instanceof ChestMenu && (block instanceof ChestBlock || block instanceof BarrelBlock));
    }
}

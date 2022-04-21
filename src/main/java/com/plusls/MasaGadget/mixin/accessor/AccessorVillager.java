package com.plusls.MasaGadget.mixin.accessor;

import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Villager.class)
public interface AccessorVillager {
    @Accessor
    int getNumberOfRestocksToday();

    @Accessor
    void setNumberOfRestocksToday(int numberOfRestocksToday);

    @Accessor
    long getLastRestockGameTime();

    @Accessor
    void setLastRestockGameTime(long lastRestockGameTime);

    @Invoker
    boolean invokeNeedsToRestock();
}

package com.plusls.MasaGadget.mixin.accessor;

import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Villager.class)
public interface AccessorVillager {
    @Accessor("numberOfRestocksToday")
    int masa_gadget_mod$getNumberOfRestocksToday();

    @Accessor("numberOfRestocksToday")
    void masa_gadget_mod$setNumberOfRestocksToday(int numberOfRestocksToday);

    @Accessor("lastRestockGameTime")
    long masa_gadget_mod$getLastRestockGameTime();

    @Accessor("lastRestockGameTime")
    void masa_gadget_mod$setLastRestockGameTime(long lastRestockGameTime);

    @Invoker("needsToRestock")
    boolean masa_gadget_mod$needsToRestock();
}

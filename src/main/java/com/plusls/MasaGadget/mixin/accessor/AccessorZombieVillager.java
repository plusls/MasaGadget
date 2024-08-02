package com.plusls.MasaGadget.mixin.accessor;

import net.minecraft.world.entity.monster.ZombieVillager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@Mixin(ZombieVillager.class)
public interface AccessorZombieVillager {
    @Accessor("villagerConversionTime")
    int masa_gadget_mod$$getVillagerConversionTime();

    @Invoker("startConverting")
    void masa_gadget_mod$startConverting(@Nullable UUID uUID, int i);
}


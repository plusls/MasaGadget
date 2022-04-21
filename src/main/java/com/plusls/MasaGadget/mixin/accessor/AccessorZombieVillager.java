package com.plusls.MasaGadget.mixin.accessor;

import net.minecraft.world.entity.monster.ZombieVillager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@Mixin(ZombieVillager.class)
public interface AccessorZombieVillager {
    @Accessor
    int getVillagerConversionTime();

    @Invoker
    void invokeStartConverting(@Nullable UUID uUID, int i);
}


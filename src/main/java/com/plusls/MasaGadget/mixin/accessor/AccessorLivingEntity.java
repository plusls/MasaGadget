package com.plusls.MasaGadget.mixin.accessor;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface AccessorLivingEntity {
    @Accessor("brain")
    void masa_gadget_mod$setBrain(Brain<?> brain);

    @Invoker("makeBrain")
    Brain<?> masa_gadget_mod$makeBrain(Dynamic<?> dynamic);
}

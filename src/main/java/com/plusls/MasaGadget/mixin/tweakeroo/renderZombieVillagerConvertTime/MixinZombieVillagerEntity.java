package com.plusls.MasaGadget.mixin.tweakeroo.renderZombieVillagerConvertTime;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillagerEntity.class)
public abstract class MixinZombieVillagerEntity extends ZombieEntity implements VillagerDataContainer {
    @Shadow
    public int conversionTimer;

    @Shadow
    protected abstract int getConversionRate();

    public MixinZombieVillagerEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void calcConversionTimer(CallbackInfo ci) {
        if (this.world.isClient() && conversionTimer > 0) {
            conversionTimer -= this.getConversionRate();
        }
    }

}

package com.plusls.MasaGadget.mixin.tweakeroo.renderZombieVillagerConvertTime;

import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.mixin.Dependencies;
import com.plusls.MasaGadget.mixin.Dependency;
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

@Dependencies(dependencyList = @Dependency(modId = ModInfo.TWEAKEROO_MOD_ID, version = "*"))
@Mixin(ZombieVillagerEntity.class)
public abstract class MixinZombieVillagerEntity extends ZombieEntity implements VillagerDataContainer {
    @Shadow
    public int conversionTimer;

    public MixinZombieVillagerEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    protected abstract int getConversionRate();

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void calcConversionTimer(CallbackInfo ci) {
        if (this.world.isClient() && conversionTimer > 0) {
            conversionTimer -= this.getConversionRate();
        }
    }

}

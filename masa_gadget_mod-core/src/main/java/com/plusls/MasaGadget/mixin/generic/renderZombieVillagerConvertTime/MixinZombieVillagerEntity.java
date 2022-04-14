package com.plusls.MasaGadget.mixin.generic.renderZombieVillagerConvertTime;

import com.plusls.MasaGadget.ModInfo;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(ZombieVillager.class)
public abstract class MixinZombieVillagerEntity {
    @Shadow
    private int villagerConversionTime;

    @Shadow
    protected abstract int getConversionProgress();

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void calcConversionTimer(CallbackInfo ci) {
        if (((ZombieVillager) (Object) this).getLevel().isClientSide() && villagerConversionTime > 0) {
            villagerConversionTime -= this.getConversionProgress();
        }
    }

}

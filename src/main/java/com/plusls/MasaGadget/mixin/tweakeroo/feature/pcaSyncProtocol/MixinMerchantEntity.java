package com.plusls.MasaGadget.mixin.tweakeroo.feature.pcaSyncProtocol;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.village.Merchant;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantEntity.class)
public abstract class MixinMerchantEntity extends PassiveEntity implements Npc, Merchant {
    @Final
    //@Dynamic
    @Shadow
    private SimpleInventory inventory;

    protected MixinMerchantEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }


    // mojang 的 SimpleInventory 实现的有问题，readTags 时不会清空原有数据需要手动清空
    @Inject(method = "readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "HEAD"))
    private void preReadTags(CompoundTag tag, CallbackInfo info) {
        if (!MinecraftClient.getInstance().isIntegratedServerRunning()) {
            this.inventory.clear();
        }
    }
}

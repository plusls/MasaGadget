package com.plusls.MasaGadget.compat.mixin.litematica.materials;

import com.plusls.MasaGadget.ModInfo;
import fi.dy.masa.litematica.materials.MaterialCache;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.LITEMATICA_MOD_ID))
@Mixin(value = MaterialCache.class, remap = false)
public abstract class MixinMaterialCache {
    @Shadow
    public abstract ItemStack getItemForState(BlockState par1);

    public ItemStack getRequiredBuildItemForState(BlockState state) {
        return this.getItemForState(state);
    }
}

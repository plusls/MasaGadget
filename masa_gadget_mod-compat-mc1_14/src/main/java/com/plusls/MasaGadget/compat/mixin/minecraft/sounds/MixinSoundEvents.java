package com.plusls.MasaGadget.compat.mixin.minecraft.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.compat.annotation.Public;
import top.hendrixshen.magiclib.compat.annotation.Remap;

@Mixin(SoundEvents.class)
public class MixinSoundEvents {

    @Public
    @Remap("field_20669")
    private static final SoundEvent VILLAGER_WORK_ARMORER_COMPAT = SoundEvents.VILLAGER_WORK_ARMORER;

    @Public
    @Remap("field_20670")
    private static final SoundEvent VILLAGER_WORK_BUTCHER_COMPAT = SoundEvents.VILLAGER_WORK_BUTCHER;

    @Public
    @Remap("field_20671")
    private static final SoundEvent VILLAGER_WORK_CARTOGRAPHER_COMPAT = SoundEvents.VILLAGER_WORK_CARTOGRAPHER;

    @Public
    @Remap("field_20672")
    private static final SoundEvent VILLAGER_WORK_CLERIC_COMPAT = SoundEvents.VILLAGER_WORK_CLERIC;

    @Public
    @Remap("field_20673")
    private static final SoundEvent VILLAGER_WORK_FARMER_COMPAT = SoundEvents.VILLAGER_WORK_FARMER;

    @Public
    @Remap("field_20674")
    private static final SoundEvent VILLAGER_WORK_FISHERMAN_COMPAT = SoundEvents.VILLAGER_WORK_FISHERMAN;

    @Public
    @Remap("field_20675")
    private static final SoundEvent VILLAGER_WORK_FLETCHER_COMPAT = SoundEvents.VILLAGER_WORK_FLETCHER;

    @Public
    @Remap("field_20676")
    private static final SoundEvent VILLAGER_WORK_LEATHERWORKER_COMPAT = SoundEvents.VILLAGER_WORK_LEATHERWORKER;

    @Public
    @Remap("field_20677")
    private static final SoundEvent VILLAGER_WORK_LIBRARIAN_COMPAT = SoundEvents.VILLAGER_WORK_LIBRARIAN;

    @Public
    @Remap("field_20678")
    private static final SoundEvent VILLAGER_WORK_MASON_COMPAT = SoundEvents.VILLAGER_WORK_MASON;

    @Public
    @Remap("field_20679")
    private static final SoundEvent VILLAGER_WORK_SHEPHERD_COMPAT = SoundEvents.VILLAGER_WORK_SHEPHERD;

    @Public
    @Remap("field_20680")
    private static final SoundEvent VILLAGER_WORK_TOOLSMITH_COMPAT = SoundEvents.VILLAGER_WORK_TOOLSMITH;

    @Public
    @Remap("field_20681")
    private static final SoundEvent VILLAGER_WORK_WEAPONSMITH_COMPAT = SoundEvents.VILLAGER_WORK_WEAPONSMITH;

}

package com.plusls.MasaGadget;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MasaGadgetMixinPlugin implements IMixinConfigPlugin {

    private static final String MALILIB_MOD_ID = "malilib";
    private static final String TWEAKEROO_MOD_ID = "tweakeroo";
    private static final String MINIHUD_MOD_ID = "minihud";
    private static final String LITEMATICA_MOD_ID = "litematica";
    private static final String BBOR_MOD_ID = "bbor";
    private static final String MIXIN_MALILIB = "com.plusls.MasaGadget.mixin.malilib.";
    private static final String MIXIN_TWEAKEROO = "com.plusls.MasaGadget.mixin.tweakeroo.";
    private static final String MIXIN_MINIHUD = "com.plusls.MasaGadget.mixin.minihud.";
    private static final String MIXIN_LITEMATICA = "com.plusls.MasaGadget.mixin.litematica.";

    public static boolean isMalilibLoaded = false;
    public static boolean isTweakerooLoaded = false;
    public static boolean isMinihudLoaded = false;
    public static boolean isLitematicaLoaded = false;
    public static boolean isBborLoaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        isMalilibLoaded = FabricLoader.getInstance().isModLoaded(MALILIB_MOD_ID);
        isTweakerooLoaded = FabricLoader.getInstance().isModLoaded(TWEAKEROO_MOD_ID);
        isMinihudLoaded = FabricLoader.getInstance().isModLoaded(MINIHUD_MOD_ID);
        isLitematicaLoaded = FabricLoader.getInstance().isModLoaded(LITEMATICA_MOD_ID);
        isBborLoaded = FabricLoader.getInstance().isModLoaded(BBOR_MOD_ID);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!isMalilibLoaded && mixinClassName.startsWith(MIXIN_MALILIB)) {
            return false;
        } else if (!isTweakerooLoaded && mixinClassName.startsWith(MIXIN_TWEAKEROO)) {
            return false;
        } else if (!isMinihudLoaded && mixinClassName.startsWith(MIXIN_MINIHUD)) {
            return false;
        } else if (!isLitematicaLoaded && mixinClassName.startsWith(MIXIN_LITEMATICA)) {
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}

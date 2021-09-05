package com.plusls.MasaGadget.mixin.litematica;

import com.plusls.MasaGadget.MasaGadgetMixinPlugin;
import com.plusls.MasaGadget.mixin.CustomDepPredicate;
import org.objectweb.asm.tree.ClassNode;

public class LitematicaDependencyPredicate {

    public static final String NUDGE_SELECTION_SUPPORT_FREECAMERA_BREAK_VERSION = "<0.0.0-dev.20210831.022621";

    static public class TweakerooPredicate implements CustomDepPredicate {
        @Override
        public boolean test(ClassNode classNode) {
            return MasaGadgetMixinPlugin.isTweakerooLoaded;
        }
    }
}

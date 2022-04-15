package com.plusls.MasaGadget.compat;

import net.fabricmc.api.ClientModInitializer;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

public class MasaGadgetModCompat implements ClientModInitializer {

    @Dependencies(and = {
            @Dependency(value = "itemscoller", versionPredicate = ">=0.16.0", optional = true),
            @Dependency(value = "litematica", versionPredicate = ">=0.11.0", optional = true),
            @Dependency(value = "minihud", versionPredicate = ">=0.22.0", optional = true),
            @Dependency(value = "tweakeroo", versionPredicate = ">=0.13.1", optional = true)
    })
    @Override
    public void onInitializeClient() {
    }
}

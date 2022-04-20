package com.plusls.MasaGadget.compat;

import net.fabricmc.api.ClientModInitializer;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

public class MasaGadgetModCompat implements ClientModInitializer {

    @Dependencies(and = {
            @Dependency(value = "itemscoller", versionPredicate = ">=0.15.0-dev.20210917.191808", optional = true),
            @Dependency(value = "litematica", versionPredicate = ">=0.0.0-dev.20210917.192300", optional = true),
            @Dependency(value = "minihud", versionPredicate = ">=0.19.0-dev.20210917.191825", optional = true),
            @Dependency(value = "tweakeroo", versionPredicate = ">=0.10.0-dev.20210917.191839", optional = true)
    })
    @Override
    public void onInitializeClient() {
    }
}

package com.plusls.MasaGadget.compat;

import net.fabricmc.api.ClientModInitializer;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

public class MasaGadgetModCompat implements ClientModInitializer {

    @Dependencies(and = {
            @Dependency(value = "itemscoller", versionPredicate = ">=0.15.0-dev.20190720.190250", optional = true),
            @Dependency(value = "litematica", versionPredicate = ">=0.0.0-dev.20191222.014040", optional = true),
            @Dependency(value = "minihud", versionPredicate = ">=0.19.0-dev.20191007.003640", optional = true),
            @Dependency(value = "tweakeroo", versionPredicate = ">=0.10.0-dev.20190903.193019", optional = true)
    })
    @Override
    public void onInitializeClient() {
    }
}

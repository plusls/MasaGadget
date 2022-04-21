package com.plusls.MasaGadget.compat.modmenu;

import net.minecraft.client.gui.screens.Screen;

public interface ConfigScreenFactoryCompat<S extends Screen> {
    S create(Screen screen);
}

package com.plusls.MasaGadget.event;

import com.plusls.MasaGadget.config.Configs;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;

public class InputHandler implements IKeybindProvider {
    private static final InputHandler INSTANCE = new InputHandler();

    public static InputHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (ConfigHotkey configHotkey : Configs.Generic.HOTKEYS) {
            manager.addKeybindToMap(configHotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
    }
}
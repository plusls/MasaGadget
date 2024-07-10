package com.plusls.MasaGadget.impl.event;

import com.plusls.MasaGadget.api.event.DisconnectListener;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.api.event.Event;

import java.util.List;

public class DisconnectEvent implements Event<DisconnectListener> {
    @Override
    public void dispatch(@NotNull List<DisconnectListener> listeners) {
        listeners.forEach(DisconnectListener::onDisconnect);
    }

    @Override
    public Class<DisconnectListener> getListenerType() {
        return DisconnectListener.class;
    }
}

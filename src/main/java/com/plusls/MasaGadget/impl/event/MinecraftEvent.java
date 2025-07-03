package com.plusls.MasaGadget.impl.event;

import com.plusls.MasaGadget.api.event.MinecraftListener;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.api.event.Event;

import java.util.List;
import java.util.function.Consumer;

public class MinecraftEvent {
    private static class GenericEvent implements Event<MinecraftListener> {
        private final Consumer<MinecraftListener> action;

        public GenericEvent(Consumer<MinecraftListener> action) {
            this.action = action;
        }

        @Override
        public void dispatch(@NotNull List<MinecraftListener> listeners) {
            for (MinecraftListener listener : listeners) {
                this.action.accept(listener);
            }
        }

        @Override
        public Class<MinecraftListener> getListenerType() {
            return MinecraftListener.class;
        }
    }

    public static class DisconnectEvent extends GenericEvent {
        public DisconnectEvent() {
            super(MinecraftListener::onDisconnect);
        }
    }

    public static class TickEndEvent extends GenericEvent {
        public TickEndEvent() {
            super(MinecraftListener::onTickEnd);
        }
    }
}

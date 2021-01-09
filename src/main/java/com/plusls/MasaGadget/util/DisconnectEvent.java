package com.plusls.MasaGadget.util;

import java.util.ArrayList;
import java.util.List;

public class DisconnectEvent {
    private static final List<Disconnect> funList = new ArrayList<>();

    public static void register(Disconnect fun) {
        funList.add(fun);
    }

    public static void onDisconnect() {
        for (Disconnect fun : funList) {
            fun.onPlayDisconnect();
        }
    }

    @FunctionalInterface
    public interface Disconnect {
        void onPlayDisconnect();
    }
}

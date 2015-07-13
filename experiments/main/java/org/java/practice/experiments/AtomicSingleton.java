package org.java.practice.experiments;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicSingleton {

    private static AtomicReference<AtomicSingleton> singleton = new AtomicReference<>();
    private static AtomicBoolean state = new AtomicBoolean(false);

    private AtomicSingleton() {
    }

    public static AtomicSingleton getInstance() {
        AtomicSingleton instance = singleton.get();
        if (instance == null) {
            if (state.compareAndSet(false, true)) {
                singleton.compareAndSet(null, new AtomicSingleton());
                instance = singleton.get();
            } else {
                while (singleton.get() == null) {
                }
            }
        }
        return instance;
    }
}

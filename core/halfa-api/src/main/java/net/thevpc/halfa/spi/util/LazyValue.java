package net.thevpc.halfa.spi.util;

public interface LazyValue<T> {
    boolean isAvailable();
    T get();
}

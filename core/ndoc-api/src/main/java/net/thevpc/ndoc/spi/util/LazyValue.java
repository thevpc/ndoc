package net.thevpc.ndoc.spi.util;

public interface LazyValue<T> {
    boolean isAvailable();
    T get();
}

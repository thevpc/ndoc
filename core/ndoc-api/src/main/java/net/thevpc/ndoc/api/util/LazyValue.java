package net.thevpc.ndoc.api.util;

public interface LazyValue<T> {
    boolean isAvailable();
    T get();
}

package net.thevpc.ntexup.api.util;

public interface NTxLazyValue<T> {
    boolean isAvailable();
    T get();
}

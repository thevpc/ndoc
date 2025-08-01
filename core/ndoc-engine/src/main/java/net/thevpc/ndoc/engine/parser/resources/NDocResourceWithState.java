package net.thevpc.ndoc.api.parser;

import java.util.Objects;

public abstract class NDocResourceWithState implements NDocResource {
    protected Object state;

    @Override
    public void save() {
        state = state();
    }

    protected abstract Object state();

    @Override
    public boolean changed() {
        Object ns = state();
        return  !Objects.equals(ns, state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NDocResourceWithState that = (NDocResourceWithState) o;
        return Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(state);
    }

    @Override
    public String toString() {
        return "DefaultHResource{" +
                "state=" + state +
                '}';
    }

    public String shortName() {
        return toString();
    }

}

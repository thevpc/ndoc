package net.thevpc.halfa.engine.resources;

import net.thevpc.halfa.api.resources.HResource;

import java.util.Objects;

abstract class DefaultHResource implements HResource {
    private Object state;

    @Override
    public void save() {
        state = state();
    }

    @Override
    public boolean changed() {
        Object ns = state();
        return !Objects.equals(ns, state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultHResource that = (DefaultHResource) o;
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
}

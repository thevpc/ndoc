package net.thevpc.halfa.api.resources;

public interface HResource {
    void save();

    boolean changed();

    Object state();

    String shortName();
}

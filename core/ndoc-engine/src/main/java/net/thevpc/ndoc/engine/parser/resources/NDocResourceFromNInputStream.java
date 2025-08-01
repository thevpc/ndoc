package net.thevpc.ndoc.engine.parser.resources;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

import java.io.InputStream;
import java.util.Objects;

public class NDocResourceFromNInputStream extends NDocResourceWithState {
    private InputStream inputStream;

    @Override
    public NOptional<NPath> path() {
        return NOptional.ofNamedEmpty("path");
    }

    public NDocResourceFromNInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Object state() {
        return inputStream;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NDocResourceFromNInputStream that = (NDocResourceFromNInputStream) o;
        return Objects.equals(inputStream, that.inputStream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inputStream);
    }
}

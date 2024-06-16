package net.thevpc.halfa.api.util;

import java.io.InputStream;
import java.util.Objects;

class NInputStreamHResource extends DefaultHResource {
    private InputStream path;

    public NInputStreamHResource(InputStream path) {
        this.path = path;
    }

    public Object state() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NInputStreamHResource that = (NInputStreamHResource) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), path);
    }
}

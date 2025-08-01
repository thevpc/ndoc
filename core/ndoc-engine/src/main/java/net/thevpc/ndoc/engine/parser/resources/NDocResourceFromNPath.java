package net.thevpc.ndoc.engine.parser.resources;

import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class NDocResourceFromNPath extends NDocResourceWithState {
    private NPath path;

    public NDocResourceFromNPath(NPath path) {
        this.path = path;
    }

    @Override
    public NOptional<NPath> path() {
        return NOptional.of(path);
    }

    public NPath getPath() {
        return path;
    }

    private static class NPathHResourceState {
        private Map<String, Object> value;

        public NPathHResourceState(Map<String, Object> value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NPathHResourceState that = (NPathHResourceState) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return "NPathHResourceState{" + value + '}';
        }
    }

    public Object state() {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("path", path.toString());
        value.put("dir", path.isDirectory());
        boolean file = path.isFile();
        value.put("file", file);
        value.put("contentLength", path.contentLength());
        Instant lastModifiedInstant = path.lastModifiedInstant();
        value.put("lastModifiedInstant", lastModifiedInstant == null ? -1 : lastModifiedInstant.getEpochSecond());
        if (path.toString().contains("*")) {
            value.put("children",
                    path.walkGlob().map(x -> new NDocResourceFromNPath(x).state()).toSet()
            );
        }
        return new NPathHResourceState(value);
    }

    @Override
    public String shortName() {
        return String.valueOf(path.getName());
    }

    @Override
    public String toString() {
        return String.valueOf(path.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NDocResourceFromNPath that = (NDocResourceFromNPath) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), path);
    }
}

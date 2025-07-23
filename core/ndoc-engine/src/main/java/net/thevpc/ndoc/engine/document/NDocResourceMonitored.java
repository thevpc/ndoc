package net.thevpc.ndoc.engine.document;

import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.api.parser.NDocResourceMonitor;
import net.thevpc.ndoc.api.parser.NDocResourceWithState;
import net.thevpc.ndoc.api.parser.NDocResourceFactory;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class NDocResourceMonitored extends NDocResourceWithState implements NDocResourceMonitor {
    private Set<NDocResource> resources = new HashSet<>();
    @Override
    public NOptional<NPath> path() {
        return NOptional.ofNamedEmpty("path");
    }

    public NDocResourceMonitored() {
    }

    @Override
    public Object state() {
        return new HResourceMonitorImplState(resources.stream().map(x -> x.state()).collect(Collectors.toSet()));
    }

    private static class HResourceMonitorImplState {
        private Set<Object> values;

        public HResourceMonitorImplState(Set<Object> values) {
            this.values = values;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HResourceMonitorImplState that = (HResourceMonitorImplState) o;
            return Objects.equals(values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(values);
        }

        @Override
        public String toString() {
            return "HResourceMonitorImplState{" +
                    values.toString()
                    + '}';
        }
    }

    public void clear() {
        for (NDocResource r : resources.toArray(new NDocResource[0])) {
            remove(r);
        }
    }

    public void remove(NDocResource r) {
        resources.remove(r);
    }

    public void add(NDocResource r) {
        if (r != null) {
            resources.add(r);
        }
    }

    @Override
    public void add(NPath r) {
        if (r != null) {
            add(NDocResourceFactory.of(r));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NDocResourceMonitored that = (NDocResourceMonitored) o;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }
}

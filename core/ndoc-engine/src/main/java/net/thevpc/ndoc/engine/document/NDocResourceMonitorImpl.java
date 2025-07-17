package net.thevpc.ndoc.engine.document;

import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.resources.NDocResourceMonitor;
import net.thevpc.ndoc.api.util.DefaultNDocResource;
import net.thevpc.ndoc.api.util.HResourceFactory;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class NDocResourceMonitorImpl extends DefaultNDocResource implements NDocResourceMonitor {
    private Set<NDocResource> resources = new HashSet<>();
    @Override
    public NOptional<NPath> path() {
        return NOptional.ofNamedEmpty("path");
    }

    public NDocResourceMonitorImpl() {
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
            add(HResourceFactory.of(r));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NDocResourceMonitorImpl that = (NDocResourceMonitorImpl) o;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }
}

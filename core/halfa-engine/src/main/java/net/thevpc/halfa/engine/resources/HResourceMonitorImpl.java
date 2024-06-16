package net.thevpc.halfa.engine.resources;

import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.resources.HResourceMonitor;
import net.thevpc.halfa.api.util.DefaultHResource;
import net.thevpc.halfa.api.util.HResourceFactory;
import net.thevpc.nuts.io.NPath;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class HResourceMonitorImpl extends DefaultHResource implements HResourceMonitor {
    private Set<HResource> resources = new HashSet<>();

    public HResourceMonitorImpl() {
    }

    @Override
    public Object state() {
        return new HResourceMonitorImplState(resources.stream().map(x->x.state()).collect(Collectors.toSet()));
    }

    private static class HResourceMonitorImplState{
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
        for (HResource r : resources.toArray(new HResource[0])) {
            remove(r);
        }
    }

    public void remove(HResource r) {
        resources.remove(r);
    }

    public void add(HResource r) {
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
        HResourceMonitorImpl that = (HResourceMonitorImpl) o;
        return Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resources);
    }
}

package net.thevpc.ndoc.engine.document;

import net.thevpc.ndoc.api.parser.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NStringBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class NDocResourceMonitored implements NDocResource, NDocResourceMonitor {
    private Set<NDocResource> resources = new HashSet<>();

    @Override
    public NOptional<NPath> path() {
        return NOptional.ofNamedEmpty("path");
    }

    public NDocResourceMonitored() {
    }


    @Override
    public void save() {
        for (NDocResource resource : resources) {
            resource.save();
        }
    }

    @Override
    public boolean changed() {
        for (NDocResource r : resources) {
            if (r.changed()) {
                return true;
            }
        }
        return false;
    }

    public String shortName() {
        return resources.stream().map(x -> x.shortName()).collect(Collectors.joining(","));
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
            NDocResource r1 = NDocResourceFactory.of(r);
            r1.save();
            add(r1);
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

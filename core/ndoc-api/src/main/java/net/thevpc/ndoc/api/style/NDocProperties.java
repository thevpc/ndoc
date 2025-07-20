package net.thevpc.ndoc.api.style;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.util.*;

public class NDocProperties {
    private NDocNode parent;
    private Map<String, NDocProp> map = new HashMap<>();

    public NDocProperties(NDocNode parent) {
        this.parent = parent;
    }

    public NDocProperties() {
    }

    public boolean containsKey(String k) {
        return map.containsKey(k);
    }

    public void set(String s, NElement value) {
        map.put(s, new NDocProp(s, value, parent));
    }

    public void set(NDocProp... c) {
        if (c != null) {
            for (NDocProp cc : c) {
                set(cc);
            }
        }
    }

    public void set(Collection<NDocProp> c) {
        if (c != null) {
            for (NDocProp cc : c) {
                set(cc);
            }
        }
    }

    public void unset(NDocProp s) {
        if (s != null) {
            map.remove(s.getName());
        }
    }

    public void unset(String s) {
        if (s != null) {
            map.remove(s);
        }
    }

    public void set(NDocProp s) {
        if (s != null) {
            map.put(s.getName(), new NDocProp(s.getName(), s.getValue(), parent));
        }
    }

    public NOptional<NDocProp> get(String... names) {
        NDocProp last = null;
        for (String name : names) {
            NDocProp hProp = map.get(name);
            if (hProp != null) {
                last = hProp;
            }
        }
        return NOptional.ofNamed(last, "style " +
                (names.length == 1 ? names[0] : Arrays.asList(names).toString())
        );
    }

    public NOptional<NDocProp> get(String s) {
        return NOptional.ofNamed(map.get(s), "style " + s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NDocProperties hStyleMap = (NDocProperties) o;
        return Objects.equals(map, hStyleMap.map);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }

    @Override
    public String toString() {
        return "Props" + map.values().toString();
    }

    public int size() {
        return map.size();
    }

    public Set<NDocProp> toSet() {
        return new HashSet<>(map.values());
    }

    public NDocProp[] toArray() {
        return toList().toArray(new NDocProp[0]);
    }

    public List<NDocProp> toList() {
        return new ArrayList<>(map.values());
    }

    public NElement toElement() {
        return NElement.ofObject(
                map.values().stream().map(x -> x.toElement()).toArray(NElement[]::new)
        );
    }

    public void clear() {
        map.clear();
    }
}

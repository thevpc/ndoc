package net.thevpc.ntexup.api.document.style;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.util.*;

public class NTxProperties {
    private NTxNode parent;
    private Map<String, NTxProp> map = new HashMap<>();

    public NTxProperties(NTxNode parent) {
        this.parent = parent;
    }

    public NTxProperties() {
    }

    public boolean containsKey(String k) {
        return map.containsKey(k);
    }

    public void set(String s, NElement value) {
        map.put(s, new NTxProp(s, value, parent));
    }

    public void set(NTxProp... c) {
        if (c != null) {
            for (NTxProp cc : c) {
                set(cc);
            }
        }
    }

    public void set(Collection<NTxProp> c) {
        if (c != null) {
            for (NTxProp cc : c) {
                set(cc);
            }
        }
    }

    public void unset(NTxProp s) {
        if (s != null) {
            map.remove(s.getName());
        }
    }

    public void unset(String s) {
        if (s != null) {
            map.remove(s);
        }
    }

    public void set(NTxProp s) {
        if (s != null) {
            map.put(s.getName(), new NTxProp(s.getName(), s.getValue(), parent));
        }
    }

    public NOptional<NTxProp> get(String... names) {
        NTxProp last = null;
        for (String name : names) {
            NTxProp hProp = map.get(name);
            if (hProp != null) {
                last = hProp;
            }
        }
        return NOptional.ofNamed(last, "style " +
                (names.length == 1 ? names[0] : Arrays.asList(names).toString())
        );
    }

    public NOptional<NTxProp> get(String s) {
        return NOptional.ofNamed(map.get(s), "style " + s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NTxProperties hStyleMap = (NTxProperties) o;
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

    public Set<NTxProp> toSet() {
        return new HashSet<>(map.values());
    }

    public NTxProp[] toArray() {
        return toList().toArray(new NTxProp[0]);
    }

    public List<NTxProp> toList() {
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

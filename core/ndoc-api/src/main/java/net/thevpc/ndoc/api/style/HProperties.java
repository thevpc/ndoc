package net.thevpc.ndoc.api.style;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.util.NOptional;

import java.util.*;

public class HProperties {
    private Map<String, HProp> map = new HashMap<>();

    public boolean containsKey(String k) {
        return map.containsKey(k);
    }

    public void set(String s, NElement value) {
        map.put(s, new HProp(s, value));
    }

    public void set(HProp... c) {
        if (c != null) {
            for (HProp cc : c) {
                set(cc);
            }
        }
    }

    public void set(Collection<HProp> c) {
        if (c != null) {
            for (HProp cc : c) {
                set(cc);
            }
        }
    }

    public void unset(HProp s) {
        if (s != null) {
            map.remove(s.getName());
        }
    }

    public void unset(String s) {
        if (s != null) {
            map.remove(s);
        }
    }

    public void set(HProp s) {
        if (s != null) {
            map.put(s.getName(), s);
        }
    }

    public NOptional<HProp> get(String... names) {
        HProp last = null;
        for (String name : names) {
            HProp hProp = map.get(name);
            if (hProp != null) {
                last = hProp;
            }
        }
        return NOptional.ofNamed(last, "style " +
                (names.length == 1 ? names[0] : Arrays.asList(names).toString())
        );
    }

    public NOptional<HProp> get(String s) {
        return NOptional.ofNamed(map.get(s), "style " + s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HProperties hStyleMap = (HProperties) o;
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

    public Set<HProp> toSet() {
        return new HashSet<>(map.values());
    }

    public HProp[] toArray() {
        return toList().toArray(new HProp[0]);
    }

    public List<HProp> toList() {
        return new ArrayList<>(map.values());
    }

    public NElement toElement() {
        return NElements.of().ofObject(
                map.values().stream().map(x -> x.toElement()).toArray(NElement[]::new)
        );
    }
}

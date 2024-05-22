package net.thevpc.halfa.api.style;

import net.thevpc.nuts.util.NOptional;

import java.util.*;

public class HStyleMap {
    private Map<HStyleType, HStyle> map = new HashMap<>();

    public void set(HStyle... c) {
        if (c != null) {
            for (HStyle cc : c) {
                set(cc);
            }
        }
    }

    public void set(Collection<HStyle> c) {
        if (c != null) {
            for (HStyle cc : c) {
                set(cc);
            }
        }
    }

    public void unset(HStyle s) {
        if (s != null) {
            map.remove(s.getName());
        }
    }

    public void unset(HStyleType s) {
        if (s != null) {
            map.remove(s);
        }
    }

    public void set(HStyle s) {
        if (s != null) {
            map.put(s.getName(), s);
        }
    }

    public NOptional<HStyle> get(HStyleType s) {
        return NOptional.ofNamed(map.get(s), "style " + s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HStyleMap hStyleMap = (HStyleMap) o;
        return Objects.equals(map, hStyleMap.map);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }

    @Override
    public String toString() {
        return "HStyleMap{" +
                map.values() +
                '}';
    }

    public int size() {
        return map.size();
    }

    public Set<HStyle> styles() {
        return new HashSet<>(map.values());
    }
}

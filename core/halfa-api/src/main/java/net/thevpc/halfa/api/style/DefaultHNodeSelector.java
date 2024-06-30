package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementBase;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultHNodeSelector implements HStyleRuleSelector {

    private static DefaultHNodeSelector ANY = new DefaultHNodeSelector(null, null, null, false);
    private static DefaultHNodeSelector IMPORTANT = new DefaultHNodeSelector(null, null, null, true);
    private final Set<String> names = new HashSet<>();
    private final Set<String> types = new HashSet<>();
    private final Set<String> classes = new HashSet<>();
    private final boolean important;

    public static DefaultHNodeSelector ofAny() {
        return ANY;
    }

    public static DefaultHNodeSelector of(String[] names, String[] types, String[] classes) {
        if (
                (names == null || names.length == 0)
                        && (types == null || types.length == 0)
                        && (classes == null || classes.length == 0)
        ) {
            return ofAny();
        }
        return new DefaultHNodeSelector(names, types, classes, false);
    }

    public static DefaultHNodeSelector ofClasses(String... cls) {
        return ANY.andClass(cls);
    }

    public static DefaultHNodeSelector ofName(String... names) {
        return ANY.andName(names);
    }

    public static DefaultHNodeSelector ofType(String... types) {
        return ANY.andType(types);
    }

    public DefaultHNodeSelector(String[] names, String[] types, String[] classes, boolean important) {
        this.important = important;
        if (names != null) {
            for (String i : names) {
                String n = NStringUtils.trimToNull(i);
                if (n != null) {
                    if (!n.equals("*")) {
                        this.names.add(n);
                    }
                }
            }
        }
        if (classes != null) {
            for (String i : classes) {
                String n = NStringUtils.trimToNull(i);
                if (n != null) {
                    if (!n.equals("*")) {
                        this.classes.add(NNameFormat.LOWER_SNAKE_CASE.format(n));
                    }
                }
            }
        }
        if (types != null) {
            for (String i : types) {
                if (i != null) {
                    this.types.add(i);
                }
            }
        }
        if (important) {
            if (
                    !this.names.isEmpty()
                            || !this.classes.isEmpty()
                            || !this.types.isEmpty()
            ) {
                throw new IllegalArgumentException("invalid important");
            }
        }
    }

    public static HStyleRuleSelector ofImportant() {
        return IMPORTANT;
    }

    public DefaultHNodeSelector andName(String... names) {
        return and(names, null, null, important);
    }

    public DefaultHNodeSelector andType(String... types) {
        return and(null, types, null, important);
    }

    public DefaultHNodeSelector andClass(String... classes) {
        return and(null, null, classes, important);
    }

    public DefaultHNodeSelector and(String[] names, String[] types, String[] classes, boolean important) {
        if (
                (names == null || names.length == 0 || (names.length == 1 && names[0] == null))
                        && (types == null || types.length == 0 || (types.length == 1 && types[0] == null))
                        && (classes == null || classes.length == 0 || (classes.length == 1 && classes[0] == null))
        ) {
            return this;
        }
        Set<String> names0 = new HashSet<>(this.names);
        Set<String> types0 = new HashSet<>(this.types);
        Set<String> classes0 = new HashSet<>(this.classes);
        if (names != null) {
            names0.addAll(Arrays.asList(names));
        }
        if (classes != null) {
            classes0.addAll(Arrays.asList(classes));
        }
        if (types != null) {
            types0.addAll(Arrays.asList(types));
        }
        if (important) {
            return IMPORTANT;
        }
        DefaultHNodeSelector c = new DefaultHNodeSelector(
                names0.toArray(new String[0]),
                types0.toArray(new String[0]),
                classes0.toArray(new String[0]),
                false
        );
        if (c.equals(this)) {
            return this;
        }
        return c;
    }

    private Set<String> computeClasses(HNode n) {
        Set<String> all = new HashSet<>();
        while (n != null) {
            all.addAll(n.styleClasses());
            n = n.parent();
        }
        return all;
    }

    @Override
    public boolean test(HNode n) {
        if (important) {
            return true;
        }
        if (!names.isEmpty()) {
            String nn = NStringUtils.trimLeftToNull(n.name());
            if (nn == null) {
                return false;
            }
            if (!names.contains(n.name())) {
                return false;
            }
        }
        if (!classes.isEmpty()) {
            Set<String> cc = computeClasses(n);
            for (String c : classes) {
                if (!cc.contains(c)) {
                    return false;
                }
            }
        }
        if (!types.isEmpty()) {
            if (!types.contains(n.type())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (important) {
            return "important";
        }
        if (types.isEmpty() && names.isEmpty() && classes.isEmpty()) {
            return "any";
        }
        StringBuilder sb = new StringBuilder();
        if (!names.isEmpty()) {
            sb.append("names(").append(names).append(")");
        }
        if (!types.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append("types(").append(types).append(")");
        }
        if (!classes.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append("classes(").append(classes).append(")");
        }
        return sb.toString();
    }

    @Override
    public TsonElement toTson() {
        if (important) {
            return Tson.ofString("$");
        }
        List<TsonElement> c = new ArrayList<>();
        if (!names.isEmpty()) {
            for (String name : names) {
                c.add(Tson.ofString(name));
            }
        }
        if (!types.isEmpty()) {
            for (String name : types) {
                c.add(Tson.name(NNameFormat.LOWER_KEBAB_CASE.format(name)));
            }
        }
        if (!classes.isEmpty()) {
            for (String name : names) {
                c.add(Tson.name("." + name));
            }
        }
        if (c.isEmpty()) {
            return Tson.ofString("*");
        }
        if (c.size() == 1) {
            return c.get(0);
        }
        return Tson.ofUplet(c.toArray(new TsonElementBase[0])).build();
    }

    @Override
    public int compareTo(HStyleRuleSelector o) {
        if (o == null) {
            return -1;
        }
        if (o instanceof DefaultHNodeSelector) {
            DefaultHNodeSelector op = (DefaultHNodeSelector) o;
            if (this.important && op.important) {
                return 0;
            }
            if (this.important) {
                return -1;
            }
            if (op.important) {
                return 1;
            }
            int c = Integer.compare(names.size(), op.names.size());
            if (c != 0) {
                return -c;
            }

            c = Integer.compare(types.size(), op.types.size());
            if (c != 0) {
                return -c;
            }

            c = Integer.compare(classes.size(), op.classes.size());
            if (c != 0) {
                return -c;
            }
            String[] a = new TreeSet<String>(names).toArray(new String[0]);
            String[] b = new TreeSet<String>(op.names).toArray(new String[0]);
            for (int j = 0; j < a.length; j++) {
                c = a[j].compareTo(b[j]);
                if (c != 0) {
                    return c;
                }
            }
            a = new TreeSet<String>(types.stream().map(x -> x).collect(Collectors.toSet())).toArray(new String[0]);
            b = new TreeSet<String>(op.types.stream().map(x -> x).collect(Collectors.toSet())).toArray(new String[0]);
            for (int j = 0; j < a.length; j++) {
                c = a[j].compareTo(b[j]);
                if (c != 0) {
                    return c;
                }
            }
            a = new TreeSet<String>(classes).toArray(new String[0]);
            b = new TreeSet<String>(op.classes).toArray(new String[0]);
            for (int j = 0; j < a.length; j++) {
                c = a[j].compareTo(b[j]);
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultHNodeSelector that = (DefaultHNodeSelector) o;
        return
                important == that.important
                        && Objects.equals(names, that.names)
                        && Objects.equals(types, that.types) && Objects.equals(classes, that.classes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(important, names, types, classes);
    }
}

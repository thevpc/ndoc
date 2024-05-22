package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NStringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultHNodeSelector implements HStyleRuleSelector {

    private static DefaultHNodeSelector ANY = new DefaultHNodeSelector(null, null, null, false);
    private static DefaultHNodeSelector IMPORTANT = new DefaultHNodeSelector(null, null, null, true);
    private final Set<String> names = new HashSet<>();
    private final Set<HNodeType> types = new HashSet<>();
    private final Set<String> classes = new HashSet<>();
    private final boolean important;

    public static DefaultHNodeSelector ofAny() {
        return ANY;
    }

    public static DefaultHNodeSelector ofClass(String cls) {
        return ANY.andClass(cls);
    }

    public static DefaultHNodeSelector ofName(String name) {
        return ANY.andName(name);
    }

    public static DefaultHNodeSelector ofType(HNodeType type) {
        return ANY.andType(type);
    }

    public DefaultHNodeSelector(String[] names, HNodeType[] types, String[] classes, boolean important) {
        this.important = important;
        if (names != null) {
            for (String i : names) {
                String n = NStringUtils.trimToNull(i);
                if (n != null) {
                    this.names.add(n);
                }
            }
        }
        if (classes != null) {
            for (String i : classes) {
                String n = NStringUtils.trimToNull(i);
                if (n != null) {
                    this.classes.add(NNameFormat.LOWER_SNAKE_CASE.format(n));
                }
            }
        }
        if (types != null) {
            for (HNodeType i : types) {
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

    public DefaultHNodeSelector andType(HNodeType... types) {
        return and(null, types, null, important);
    }

    public DefaultHNodeSelector andClass(String... classes) {
        return and(null, null, classes, important);
    }

    public DefaultHNodeSelector and(String[] names, HNodeType[] types, String[] classes, boolean important) {
        if (
                (names == null || names.length == 0 || (names.length == 1 && names[0] == null))
                        && (types == null || types.length == 0 || (types.length == 1 && types[0] == null))
                        && (classes == null || classes.length == 0 || (classes.length == 1 && classes[0] == null))
        ) {
            return this;
        }
        Set<String> names0 = new HashSet<>(this.names);
        Set<HNodeType> types0 = new HashSet<>(this.types);
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
                types0.toArray(new HNodeType[0]),
                classes0.toArray(new String[0]),
                false
        );
        if (c.equals(this)) {
            return this;
        }
        return c;
    }

    @Override
    public boolean test(HNode n) {
        if (important) {
            return true;
        }
        if (!names.isEmpty()) {
            String nn = NStringUtils.trimLeftToNull(n.name());
            if (nn != null) {
                if (!names.contains(n.name())) {
                    return false;
                }
            }
        }
        if (!classes.isEmpty()) {
            for (String c : classes) {
                if (!n.hasClass(c)) {
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
            a = new TreeSet<String>(types.stream().map(x -> x.name()).collect(Collectors.toSet())).toArray(new String[0]);
            b = new TreeSet<String>(op.types.stream().map(x -> x.name()).collect(Collectors.toSet())).toArray(new String[0]);
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

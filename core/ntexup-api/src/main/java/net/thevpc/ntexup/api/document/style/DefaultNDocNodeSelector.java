package net.thevpc.ntexup.api.document.style;

import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NStringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultNDocNodeSelector implements NDocStyleRuleSelector {

    private static DefaultNDocNodeSelector ANY = new DefaultNDocNodeSelector(null, null, null, false);
    private static DefaultNDocNodeSelector IMPORTANT = new DefaultNDocNodeSelector(null, null, null, true);
    private final Set<String> names = new HashSet<>();
    private final Set<String> types = new HashSet<>();
    private final Set<String> classes = new HashSet<>();
    private final boolean important;

    public static DefaultNDocNodeSelector ofAny() {
        return ANY;
    }

    public static DefaultNDocNodeSelector of(String[] names, String[] types, String[] classes) {
        if (
                (names == null || names.length == 0)
                        && (types == null || types.length == 0)
                        && (classes == null || classes.length == 0)
        ) {
            return ofAny();
        }
        return new DefaultNDocNodeSelector(names, types, classes, false);
    }

    public static DefaultNDocNodeSelector ofClasses(String... cls) {
        return ANY.andClass(cls);
    }

    public static DefaultNDocNodeSelector ofName(String... names) {
        return ANY.andName(names);
    }

    public static DefaultNDocNodeSelector ofType(String... types) {
        return ANY.andType(types);
    }

    public DefaultNDocNodeSelector(String[] names, String[] types, String[] classes, boolean important) {
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

    public static NDocStyleRuleSelector ofImportant() {
        return IMPORTANT;
    }

    public DefaultNDocNodeSelector andName(String... names) {
        return and(names, null, null, important);
    }

    public DefaultNDocNodeSelector andType(String... types) {
        return and(null, types, null, important);
    }

    public DefaultNDocNodeSelector andClass(String... classes) {
        return and(null, null, classes, important);
    }

    public DefaultNDocNodeSelector and(String[] names, String[] types, String[] classes, boolean important) {
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
        DefaultNDocNodeSelector c = new DefaultNDocNodeSelector(
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

    private Set<String> computeClasses(NTxItem n) {
        Set<String> all = new HashSet<>();
        while (n != null) {
            if (n instanceof NTxNode) {
                all.addAll(((NTxNode)n).styleClasses());
            }
            n = n.parent();
        }
        return all;
    }

    @Override
    public boolean acceptNode(NTxNode n) {
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
    public NElement toElement() {
        if (important) {
            return NElement.ofString("$");
        }
        List<NElement> c = new ArrayList<>();
        if (!names.isEmpty()) {
            for (String name : names) {
                c.add(NElement.ofString(name));
            }
        }
        if (!types.isEmpty()) {
            for (String name : types) {
                c.add(NElement.ofName(NNameFormat.LOWER_KEBAB_CASE.format(name)));
            }
        }
        if (!classes.isEmpty()) {
            for (String name : classes) {
                c.add(NElement.ofName("." + name));
            }
        }
        if (c.isEmpty()) {
            return NElement.ofString("*");
        }
        if (c.size() == 1) {
            return c.get(0);
        }
        return NElement.ofUplet(c.toArray(new NElement[0]));
    }

    @Override
    public int compareTo(NDocStyleRuleSelector o) {
        if (o == null) {
            return -1;
        }
        if (o instanceof DefaultNDocNodeSelector) {
            DefaultNDocNodeSelector op = (DefaultNDocNodeSelector) o;
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
        DefaultNDocNodeSelector that = (DefaultNDocNodeSelector) o;
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

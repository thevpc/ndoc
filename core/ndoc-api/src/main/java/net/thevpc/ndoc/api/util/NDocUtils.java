package net.thevpc.ndoc.api.util;

import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class NDocUtils {

    public static final String COMPONENT_BODY_VAR_NAME = "componentBody";
    public static final String COMPILER_DECLARATION_PATH = "CompilerDeclarationPath";

    public static boolean hasCompilerDeclarationPath(NElement element) {
        return element.annotations().stream().anyMatch(x -> x.name().equals(COMPILER_DECLARATION_PATH));
    }

    public static NOptional<String> findCompilerDeclarationPath(NElement element) {
        Optional<String> e = element.annotations().stream().filter(x -> COMPILER_DECLARATION_PATH.equals(x.name())).flatMap(x -> x.params().get(0).asStringValue().stream().stream()).findFirst();
        return NOptional.ofOptional(e, NMsg.ofC("Missing CompilerDeclarationPath in %s", element));
    }

    public static NElement addCompilerDeclarationPath(NElement elem, NDocResource resource) {
        if (resource != null) {
            NPath nPath = resource.path().orNull();
            if (nPath != null) {
                NOptional<String> o = findCompilerDeclarationPath(elem);
                if (o.isPresent()) {
                    return elem;
                }
                return addCompilerDeclarationPath(elem, nPath.toString());
            }
        }
        return elem;
    }

    public static NElement addCompilerDeclarationPathDummy(NElement element) {
        NOptional<String> o = findCompilerDeclarationPath(element);
        if (o.isPresent()) {
            return element;
        }
        return element.builder().addAnnotation(COMPILER_DECLARATION_PATH, NElement.ofString("")).build();
    }

    public static NElement addCompilerDeclarationPath(NElement element, String path) {
        NOptional<String> o = findCompilerDeclarationPath(element);
        if (o.isPresent()) {
            return element;
        }
        return element.builder().addAnnotation(COMPILER_DECLARATION_PATH, NElement.ofString(path)).build();
    }

    public static List<NDocNode> nodePath(NDocNode node) {
        List<NDocNode> all = new ArrayList<>();
        NDocItem i = node;
        while (i != null) {
            if (i instanceof NDocItem) {
                all.add(0, (NDocNode) i);
            }
            i = i.parent();
        }
        return all;
    }

    public static NDocNode firstNodeUp(NDocItem item) {
        while (item != null) {
            if (item instanceof NDocNode) {
                return (NDocNode) item;
            }
            item = item.parent();
        }
        return null;
    }

    public static final MinMax minMaxZ(NDocPoint3D[] points) {
        MinMax m = new MinMax();
        for (NDocPoint3D point : points) {
            m.registerValue(point.z);
        }
        return m;
    }

    public static final double[] dtimes(double min, double max, int times) {
        double[] d = new double[times];
        if (times == 1) {
            d[0] = min;
        } else {
            double step = (max - min) / (times - 1);
            for (int i = 0; i < d.length; i++) {
                d[i] = min + i * step;
            }
        }
        return d;
    }

    public static String getCompilerDeclarationPath(NElement element) {
        return element.annotations().stream().filter(a -> a.name().equals(COMPILER_DECLARATION_PATH)).findFirst().map(x -> x.param(0).asStringValue().get()).orElse(null);

    }


    public static NPath resolvePath(NElement path, Object source) {
        if (NBlankable.isBlank(path)) {
            return null;
        }
        String pathString = path.asStringValue().get();
        if (NBlankable.isBlank(pathString)) {
            return null;
        }
        String compilerDeclarationPath = getCompilerDeclarationPath(path);
        NPath referencePath = null;
        if (compilerDeclarationPath != null) {
            referencePath = NPath.of(compilerDeclarationPath);
        } else {
            if (source != null) {
                if (source instanceof NPath) {
                    referencePath = (NPath) source;
                } else if (source instanceof NDocResource) {
                    referencePath = ((NDocResource) source).path().orNull();
                }
            }
        }
        NPath base;
        if (referencePath != null) {
            if (referencePath.isRegularFile()) {
                referencePath = referencePath.getParent();
            }
            if (referencePath != null) {
                base = referencePath.resolve(pathString);
            } else {
                base = NPath.of(pathString);
            }
        } else {
            base = NPath.of(pathString);
        }
        return base;
    }

    public static String shortName(NDocResource a) {
        if (a == null) {
            return null;
        }
        return a.shortName();
    }

    public static String strSnapshot(Object a) {
        if (a == null) {
            return "null";
        }
        ;
        for (String s : a.toString().split("\n")) {
            s = s.trim();
            if (!s.isEmpty()) {
                return s + "...";
            }
        }
        return "";
    }

    public static Double min(Double a, Double b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (a < b) {
            return a;
        }
        return b;
    }

    public static Double max(Double a, Double b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (a < b) {
            return b;
        }
        return a;
    }

    public static double doubleOf(Number n) {
        if (n == null) {
            return 0;
        }
        return n.doubleValue();
    }

    public static int intOf(Number n) {
        if (n == null) {
            return 0;
        }
        return n.intValue();
    }

    public static NElement toElement(
            Object o
    ) {
        if (o == null) {
            return NElement.ofNull();
        }
        if (o instanceof String) {
            return NElement.ofString((String) o);
        }
        if (o instanceof Double) {
            return NElement.ofDouble((Double) o);
        }
        if (o instanceof Integer) {
            return NElement.ofInt((Integer) o);
        }
        if (o instanceof Boolean) {
            return NElement.ofBoolean((Boolean) o);
        }
        if (o instanceof NElement) {
            return (NElement) o;
        }
        if (o instanceof Point2D.Double) {
            return NElement.ofUplet(
                    toElement(((Point2D.Double) o).getX()),
                    toElement(((Point2D.Double) o).getY())
            );
        }
        if (o instanceof NToElement) {
            return ((NToElement) o).toElement();
        }
        if (o instanceof Enum) {
            return NElement.ofName(
                    NNameFormat.LOWER_KEBAB_CASE.format(((Enum<?>) o).name())
            );
        }
        if (o instanceof Color) {
            Color c = (Color) o;
            return NElement.ofString(String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
        }
        if (o.getClass().isArray()
        ) {
            if (o.getClass().getComponentType().isPrimitive()) {
                List<NElement> a = new ArrayList<>();
                int max = Array.getLength(o);
                for (int i = 0; i < max; i++) {
                    a.add(toElement(Array.get(o, i)));
                }
                return NElement.ofArray(a.toArray(new NElement[0]));
            } else {
                return
                        NElement.ofArray(
                                Arrays.stream((Object[]) o)
                                        .map(x -> toElement(x))
                                        .toArray(NElement[]::new)
                        );
            }
        }
        throw new IllegalArgumentException("Unsupported toElement(" + o.getClass().getName() + ")");
    }

    public static Object fromTson(NElement v) {
        if (v == null) {
            return null;
        }
        switch (v.type()) {
            case INT:
                return v.asIntValue().get();
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
                return v.asStringValue().get();
        }
        throw new IllegalArgumentException("unsupported yet : fromTson(" + v.type() + ")");
    }

    public static String[] uids(String[]... ids) {
        LinkedHashSet<String> all = new LinkedHashSet<>();
        if (ids != null) {
            for (String[] ids1 : ids) {
                if (ids1 != null) {
                    for (String s : ids1) {
                        s = NStringUtils.trimToNull(s);
                        if (s != null) {
                            all.add(uid(s));
                        }
                    }
                }
            }
        }
        return all.toArray(new String[0]);
    }

    public static String uid(String id) {
        return NNameFormat.LOWER_KEBAB_CASE.format(NStringUtils.trim(id));
    }

    public static Color paintAsColor(Paint paint) {
        if (paint instanceof Color) {
            return (Color) paint;
        }
        return null;
    }

    public static Paint resolveForegroundColor(NDocTextOptions options) {
        if (options.foregroundColorIndex != null) {
            return Colors.resolveDefaultColorByIndex(options.foregroundColorIndex, null);
        } else if (options.foregroundColor instanceof Color) {
            return options.foregroundColor;
        }
        return null;
    }

    public static boolean asBoolean(NElement e) {
        switch (e.type()) {
            case BOOLEAN:
                return e.asBooleanValue().get();
            default: {
                if (e.isNumber()) {
                    return e.asDoubleValue().get().doubleValue() != 0.0;
                }
                return !e.isNull();
            }
        }
    }

    public static List<Object> asListOfObjects(Object anyVal) {
        if (anyVal instanceof Collection) {
            return new ArrayList<>((Collection) anyVal);
        }
        if (anyVal instanceof Object[]) {
            return new ArrayList<>(Arrays.asList((Object[]) anyVal));
        }
        if (anyVal == null) {
            return new ArrayList<>();
        }
        if (anyVal.getClass().isArray()) {
            List<Object> a = new ArrayList<>();
            int max = Array.getLength(anyVal);
            for (int i = 0; i < max; i++) {
                a.add(Array.get(anyVal, i));
            }
            return a;
        }
        return Arrays.asList(anyVal);
    }

    public static Map<String, NElement> inheritedVarsMap(NDocItem c) {
        Map<String, NElement> r = new HashMap<>();
        NDocItem i = c;
        while (i != null) {
            if (i instanceof NDocNode) {
                for (Map.Entry<String, NElement> e : ((NDocNode) i).getVars().entrySet()) {
                    String k = e.getKey();
                    if (!r.containsKey(k)) {
                        r.put(k, e.getValue());
                    }
                }
            }
            i = i.parent();
        }
        return r;
    }

    public static NDocResource sourceOf(NDocItem node) {
        while (node != null) {
            NDocResource s = node.source();
            if (s != null) {
                return s;
            }
            node = node.parent();
        }
        return null;
    }

    public static NElement removeCompilerDeclarationPathAnnotations(NElement yy) {
        return yy.transform(new NElementTransform() {
            @Override
            public NElement[] preTransform(NElement element) {
                List<NElementAnnotation> oldAnn = element.annotations();
                List<NElementAnnotation> a = oldAnn.stream().filter(x -> !COMPILER_DECLARATION_PATH.equals(x.name())).collect(Collectors.toList());
                if (a.size() != oldAnn.size()) {
                    NElementBuilder b = element.builder().clearAnnotations().addAnnotations(a);
                    return new NElement[]{b.build()};
                }
                return new NElement[]{element};
            }
        })[0];
    }

    public static NElement addCompilerDeclarationPathAnnotations(NElement yy, String source) {
        if (NBlankable.isBlank(source)) {
            return yy;
        }
        return yy.transform(new NElementTransform() {
            @Override
            public NElement[] postTransform(NElement element) {
                if(element.isString()) {
                    List<NElementAnnotation> oldAnn = element.annotations();
                    List<NElementAnnotation> a = oldAnn.stream().filter(x -> !COMPILER_DECLARATION_PATH.equals(x.name())).collect(Collectors.toList());
                    if (a.size() == oldAnn.size()) {
                        NElementBuilder b = element.builder().addAnnotation(COMPILER_DECLARATION_PATH, NElement.ofString(source)).addAnnotations(a);
                        return new NElement[]{b.build()};
                    }
                }
                return new NElement[]{element};
            }
        })[0];
    }

    public static String snippet(NElement yy) {
        yy = removeCompilerDeclarationPathAnnotations(yy);
        return yy.snippet();
    }


    public static void setNodeParent(NDocItem item, NDocNode parent) {
        if (item instanceof NDocNode) {
            ((NDocNode) item).setParent(parent);
        } else if (item instanceof NDocItemList) {
            for (NDocItem nDocItem : ((NDocItemList) item).getItems()) {
                setNodeParent(nDocItem, parent);
            }
        }
    }

    public static String snippet(NDocNode node) {
        if (node == null) {
            return "null";
        }
        int size = 100;
        String s = node.toString();
        int u = s.indexOf("\n");
        boolean truncated = false;
        if (u >= 0) {
            s = s.substring(0, u);
            truncated = true;
        }
        if (s.length() > size) {
            s = s.substring(0, size);
            truncated = true;
        }
        if (truncated) {
            return s + "...";
        }
        return s;
    }

    public static void checkNode(NDocNode node) {
        checkNode(node, true);
    }

    public static void checkNode(NDocNode node, boolean recursive) {
        checkNode(node, (NDocNode) (node.parent()), recursive);

    }

    public static void checkNode(NDocNode node, NDocNode expectedRoot) {
        checkNode(node, expectedRoot, true);
    }

    public static void checkNode(NDocNode node, NDocNode expectedRoot, boolean recursive) {
//        NAssert.requireEquals(node.parent(), expectedRoot, "root for " + snippet(node));
//        NAssert.requireNonNull(node.type(), "type for " + snippet(node));
//        NAssert.requireNonNull(node.source(), "source for " + snippet(node));
//        NAssert.requireNonNull(node.uuid(), "uuid for " + snippet(node));
//        NAssert.requireNonNull(node.uuid(), "uuid for " + snippet(node));
//        for (NDocNode child : node.children()) {
//            checkNode(child, node, recursive);
//        }
    }

    public static boolean isAnyDefVarName(String name) {
        return NNameFormat.equalsIgnoreFormat(COMPONENT_BODY_VAR_NAME, name);
    }
}

package net.thevpc.ndoc.api.util;

import net.thevpc.ndoc.api.model.elem3d.HPoint3D;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.spi.renderer.text.NDocTextOptions;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.elem.NToElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class HUtils {
    public static final MinMax minMaxZ(HPoint3D[] points) {
        MinMax m = new MinMax();
        for (HPoint3D point : points) {
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
        return element.annotations().stream().filter(a -> a.name().equals("CompilerDeclarationPath")).findFirst().map(x -> x.param(0).stringValue()).orElse(null);

    }

    public static NElement addCompilerDeclarationPath(NElement element, String path) {
        return element.builder().addAnnotation("CompilerDeclarationPath", NElements.of().ofString(path)).build();
    }

    public static NPath resolvePath(NElement path, Object source) {
        if (NBlankable.isBlank(path)) {
            return null;
        }
        String pathString = path.stringValue();
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
                } else if (source instanceof HResource) {
                    referencePath = ((HResource) source).path().orNull();
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

    public static String shortName(HResource a) {
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
            return NElements.of().ofNull();
        }
        if (o instanceof String) {
            return NElements.of().ofString((String) o);
        }
        if (o instanceof Double) {
            return NElements.of().ofDouble((Double) o);
        }
        if (o instanceof Integer) {
            return NElements.of().ofInt((Integer) o);
        }
        if (o instanceof Boolean) {
            return NElements.of().ofBoolean((Boolean) o);
        }
        if (o instanceof NElement) {
            return (NElement) o;
        }
        if (o instanceof Point2D.Double) {
            return NElements.of().ofUplet(
                    toElement(((Point2D.Double) o).getX()),
                    toElement(((Point2D.Double) o).getY())
            );
        }
        if (o instanceof NToElement) {
            return ((NToElement) o).toElement();
        }
        if (o instanceof Enum) {
            return NElements.of().ofName(
                    NNameFormat.LOWER_KEBAB_CASE.format(((Enum<?>) o).name())
            );
        }
        if (o instanceof Color) {
            Color c = (Color) o;
            return NElements.of().ofString(String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
        }
        if (o.getClass().isArray()
        ) {
            if (o.getClass().getComponentType().isPrimitive()) {
                List<NElement> a = new ArrayList<>();
                int max = Array.getLength(o);
                for (int i = 0; i < max; i++) {
                    a.add(toElement(Array.get(o, i)));
                }
                return NElements.of().ofArray(a.toArray(new NElement[0]));
            } else {
                return
                        NElements.of().ofArray(
                                Arrays.stream((Object[]) o)
                                        .map(x -> toElement(x))
                                        .toArray(NElement[]::new)
                        );
            }
        }
        throw new IllegalArgumentException("Unsupported toTson(" + o.getClass().getName() + ")");
    }

    public static Object fromTson(NElement v) {
        if (v == null) {
            return null;
        }
        switch (v.type()) {
            case INTEGER:
                return v.toInt().intValue();
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
                return v.toStr().intValue();
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
        if(paint instanceof Color) {
            return (Color) paint;
        }
        return null;
    }
    public static Paint resolveForegroundColor(NDocTextOptions options) {
        if(options.foregroundColorIndex!=null){
            return Colors.resolveDefaultColorByIndex(options.foregroundColorIndex,null);
        }else if(options.foregroundColor instanceof Color){
            return options.foregroundColor;
        }
        return null;
    }
}

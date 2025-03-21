package net.thevpc.halfa.api.util;

import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.spi.renderer.text.HTextOptions;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementBase;

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

    public static String getCompilerDeclarationPath(TsonElement element) {
        return element.annotations().stream().filter(a -> a.name().equals("CompilerDeclarationPath")).findFirst().map(x -> x.param(0).stringValue()).orElse(null);

    }

    public static TsonElement addCompilerDeclarationPath(TsonElement element, String path) {
        return element.builder().addAnnotation("CompilerDeclarationPath", Tson.of(path)).build();
    }

    public static NPath resolvePath(TsonElement path, Object source) {
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

    public static TsonElement toTson(
            Object o
    ) {
        if (o == null) {
            return Tson.ofNull();
        }
        if (o instanceof String) {
            return Tson.ofString((String) o);
        }
        if (o instanceof Double) {
            return Tson.ofDouble((Double) o);
        }
        if (o instanceof Integer) {
            return Tson.ofInt((Integer) o);
        }
        if (o instanceof Boolean) {
            return Tson.ofBoolean((Boolean) o);
        }
        if (o instanceof TsonElement) {
            return (TsonElement) o;
        }
        if (o instanceof TsonElementBase) {
            return ((TsonElementBase) o).build();
        }
        if (o instanceof Point2D.Double) {
            return Tson.ofUplet(
                    toTson(((Point2D.Double) o).getX()),
                    toTson(((Point2D.Double) o).getY())
            ).build();
        }
        if (o instanceof ToTson) {
            return ((ToTson) o).toTson();
        }
        if (o instanceof Enum) {
            return Tson.ofName(
                    NNameFormat.LOWER_KEBAB_CASE.format(((Enum<?>) o).name())
            );
        }
        if (o instanceof Color) {
            Color c = (Color) o;
            return Tson.ofString(String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
        }
        if (o.getClass().isArray()
        ) {
            if (o.getClass().getComponentType().isPrimitive()) {
                List<TsonElement> a = new ArrayList<>();
                int max = Array.getLength(o);
                for (int i = 0; i < max; i++) {
                    a.add(toTson(Array.get(o, i)));
                }
                return Tson.ofArray(a.toArray(new TsonElementBase[0])).build();
            } else {
                return
                        Tson.ofArray(
                                Arrays.stream((Object[]) o)
                                        .map(x -> toTson(x))
                                        .toArray(TsonElementBase[]::new)
                        ).build();
            }
        }
        throw new IllegalArgumentException("Unsupported toTson(" + o.getClass().getName() + ")");
    }

    public static Object fromTson(TsonElement v) {
        if (v == null) {
            return null;
        }
        switch (v.type()) {
            case INTEGER:
                return v.toInt().intValue();
            case STRING:
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
    public static Paint resolveForegroundColor(HTextOptions options) {
        if(options.foregroundColorIndex!=null){
            return Colors.resolveDefaultColorByIndex(options.foregroundColorIndex,null);
        }else if(options.foregroundColor instanceof Color){
            return options.foregroundColor;
        }
        return null;
    }
}

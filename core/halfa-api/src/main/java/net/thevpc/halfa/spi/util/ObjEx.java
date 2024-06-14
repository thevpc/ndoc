package net.thevpc.halfa.spi.util;

import net.thevpc.halfa.api.model.HArrayHead;
import net.thevpc.halfa.api.model.elem2d.*;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NLiteral;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.*;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

public class ObjEx {
    private Object element;
    private String name;
    private List<TsonElement> args = new ArrayList<>();
    private List<TsonElement> children = new ArrayList<>();

    private boolean parsedChildren;

    public static ObjEx ofProp(HNode n, String name) {
        if (n == null) {
            return of(null);
        }
        return of(n.getPropertyValue(name).orNull());
    }

    public static ObjEx of(Object element) {
        return new ObjEx(element);
    }

    public ObjEx(Object element) {
        if (element instanceof TsonElementBuilder) {
            element = ((TsonElementBuilder) element).build();
        }
        this.element = element;
    }

    private void _parsedChildren() {
        if (!parsedChildren) {
            parsedChildren = true;
            if (element instanceof TsonContainer) {
                TsonContainer te = (TsonContainer) element;
                switch (te.type()) {
                    case FUNCTION:
                    case OBJECT:
                    case ARRAY: {
                        name = HUtils.uid(te.name());
                        TsonElementList a = te.args();
                        if (a != null) {
                            args.addAll(a.toList());
                        }
                        a = te.body();
                        if (a != null) {
                            children.addAll(a.toList());
                        }
                        break;
                    }
                }
            }

        }
    }

    public NOptional<SimplePair> asSimplePair() {
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case PAIR: {
                    TsonElement key = te.toPair().getKey();
                    NOptional<String> s = new ObjEx(key).asString();
                    if (s.isPresent()) {
                        return NOptional.of(
                                new SimplePair(
                                        s.get(),
                                        key,
                                        new ObjEx(te.toPair().getValue())
                                )
                        );
                    }
                    break;
                }
            }
        }
        return NOptional.ofNamedEmpty("pair");
    }


    public String name() {
        _parsedChildren();
        return name;
    }

    public Map<String, ObjEx> argsOrBodyMap() {
        Map<String, ObjEx> a = new HashMap<>();
        for (TsonElement arg : args()) {
            NOptional<SimplePair> sp = ObjEx.of(arg).asSimplePair();
            if (sp.isPresent()) {
                a.put(HUtils.uid(sp.get().name), sp.get().value);
            }
        }
        for (TsonElement arg : body()) {
            NOptional<SimplePair> sp = ObjEx.of(arg).asSimplePair();
            if (sp.isPresent()) {
                a.put(HUtils.uid(sp.get().name), sp.get().value);
            }
        }
        return a;
    }

    public Map<String, ObjEx> argsMap() {
        Map<String, ObjEx> a = new HashMap<>();
        for (TsonElement arg : args()) {
            NOptional<SimplePair> sp = ObjEx.of(arg).asSimplePair();
            if (sp.isPresent()) {
                a.put(HUtils.uid(sp.get().name), sp.get().value);
            }
        }
        return a;
    }

    public List<TsonElement> args() {
        _parsedChildren();
        return args;
    }

    public List<TsonElement> body() {
        _parsedChildren();
        return children;
    }

    public NOptional<Color> parseColor(ColorPalette palette) {
        if (element instanceof Color) {
            return NOptional.of((Color) element);
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case BYTE:
                case BIG_INT:
                case SHORT:
                case LONG:
                case INT: {
                    return new ObjEx(te.toInt().getInt()).parseColor(palette);
                }
                case UPLET: {
                    NOptional<int[]> ri = asIntArray();
                    if (ri.isPresent()) {
                        int[] ints = ri.get();
                        if (ints.length == 3 || ints.length == 4) {
                            return NOptional.of(
                                    new Color(
                                            ints[0],
                                            ints[1],
                                            ints[2],
                                            ints.length == 3 ? 255 : ints[3]
                                    )
                            );
                        }
                    }
                    NOptional<float[]> rd = asFloatArray();
                    if (rd.isPresent()) {
                        float[] ints = rd.get();
                        if (ints.length == 3 || ints.length == 4) {
                            return NOptional.of(
                                    new Color(
                                            ints[0],
                                            ints[1],
                                            ints[2],
                                            ints.length == 3 ? 1.0f : ints[3]
                                    )
                            );
                        }
                    }
                    break;
                }
                case STRING:
                case NAME: {
                    ObjEx h = new ObjEx(element);
                    String s = h.asString().get();
                    return new ObjEx(s).parseColor(palette);
                }
            }
        } else {
            if (
                    element instanceof Integer
                            || element instanceof Short
                            || element instanceof Byte
                            || element instanceof Long
                            || element instanceof BigInteger
            ) {
                return NOptional.of(new Color(((Number) element).intValue()));
            }
            if (element instanceof String) {
                String s = (String) element;
                if (s.startsWith("#")) {
                    return NOptional.of(new Color(Integer.parseInt(s.substring(1), 16)));
                }
                if (s.indexOf(",") >= 0) {
                    String[] a = s.split(",");
                    if (a.length == 3 || a.length == 4) {
                        ObjEx r = ObjEx.of(a[0]);
                        ObjEx g = ObjEx.of(a[1]);
                        ObjEx b = ObjEx.of(a[2]);
                        ObjEx aa = ObjEx.of(a.length == 4 ? a[3] : null);
                        if (r.asInt().isPresent() && g.asInt().isPresent() && b.asInt().isPresent()) {
                            return NOptional.of(
                                    new Color(
                                            r.asInt().get(),
                                            g.asInt().get(),
                                            b.asInt().get(),
                                            aa.asInt().orElse(255)
                                    )
                            );
                        }
                        if (r.asDouble().isPresent() && g.asDouble().isPresent() && b.asDouble().isPresent()) {
                            return NOptional.of(
                                    new Color(
                                            (r.asFloat().get()),
                                            (g.asFloat().get()),
                                            (b.asFloat().get()),
                                            aa.asFloat().orElse(1.0f)
                                    )
                            );
                        }
                    }
                }
                if (s.toLowerCase().startsWith("c")) {
                    NOptional<Integer> u = NLiteral.of(s.substring(1).trim()).asInt();
                    if (u.isPresent()) {
                        if (palette != null) {
                            Color c = palette.getColor(u.get());
                            if (c != null) {
                                return NOptional.of(c);
                            }
                        }
                        return NOptional.of(DefaultColorPalette.INSTANCE.getColor(u.get()));
                    }
                }
                switch (NNameFormat.LOWER_KEBAB_CASE.format(s.toLowerCase())) {
                    case "red": {
                        return NOptional.of(Color.RED);
                    }
                    case "blue": {
                        return NOptional.of(Color.BLUE);
                    }
                    case "black": {
                        return NOptional.of(Color.BLACK);
                    }
                    case "white": {
                        return NOptional.of(Color.WHITE);
                    }
                    case "yellow": {
                        return NOptional.of(Color.YELLOW);
                    }
                    case "cyan": {
                        return NOptional.of(Color.CYAN);
                    }
                    case "orange": {
                        return NOptional.of(Color.ORANGE);
                    }
                    case "pink": {
                        return NOptional.of(Color.PINK);
                    }
                    case "dark-gray": {
                        return NOptional.of(Color.DARK_GRAY);
                    }
                    case "light-gray": {
                        return NOptional.of(Color.LIGHT_GRAY);
                    }
                    case "gray": {
                        return NOptional.of(Color.GRAY);
                    }
                    case "green": {
                        return NOptional.of(Color.GREEN);
                    }
                    case "magenta": {
                        return NOptional.of(Color.MAGENTA);
                    }
                }
                try {
                    int z = Integer.parseInt(s, 16);
                    return NOptional.of(new Color(z));
                } catch (Exception e) {

                }
                try {
                    int z = Integer.parseInt(s);
                    return NOptional.of(new Color(z));
                } catch (Exception e) {
                    //
                }
            }
        }
        return NOptional.ofNamedEmpty("color from " + element);
    }


    public NOptional<Float> asFloat() {
        return asDouble().map(Double::floatValue);
    }

    public NOptional<Double> asDouble() {
        if (
                element instanceof Double
                        || element instanceof Float
                        || element instanceof BigDecimal
        ) {
            return NOptional.of(((Number) element).doubleValue());
        }
        if (element instanceof String) {
            return NLiteral.of(element).asDouble();
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            if (te.type().isNumber()) {
                switch (te.type()) {
                    case BYTE:
                    case INT:
                    case SHORT:
                    case LONG:
                    case FLOAT:
                    case DOUBLE:
                    case BIG_DECIMAL: {
                        return NOptional.of(te.toNumber().getDouble());
                    }
                }
                return NOptional.of(te.toDouble().getValue());
            } else if (te.type() == TsonElementType.STRING) {
                return ObjEx.of(te.toStr().raw()).asDouble();
            } else {
                return NOptional.ofNamedEmpty("double from " + element);
            }
        }
        return NOptional.ofNamedEmpty("double from " + element);
    }

    public NOptional<Integer> asInt() {
        if (
                element instanceof Integer
                        || element instanceof Long
                        || element instanceof Byte
                        || element instanceof Short
                        || element instanceof BigInteger
        ) {
            return NOptional.of(((Number) element).intValue());
        }
        if (element instanceof String) {
            return NLiteral.of(element).asInt();
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            if (te.type().isNumber()) {
                switch (te.type()) {
                    case BYTE:
                    case SHORT:
                    case INT:
                    case LONG: {
                        return NOptional.of(te.toNumber().getInt());
                    }
                }
            } else if (te.type() == TsonElementType.STRING) {
                return ObjEx.of(te.toStr().raw()).asInt();
            } else {
                return NOptional.ofNamedEmpty("double from " + element);
            }
        }
        return NOptional.ofNamedEmpty("double from " + element);
    }

    public NOptional<Boolean> asBoolean() {
        if (element instanceof Boolean) {
            return NOptional.of(((Boolean) element));
        }
        if (element instanceof String) {
            return NLiteral.of(element).asBoolean();
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case BOOLEAN: {
                    return NOptional.of(te.toBoolean().getBoolean());
                }
                case STRING: {
                    return NLiteral.of(te.toStr().raw()).asBoolean();
                }
                case NAME: {
                    return NLiteral.of(te.toName().getName()).asBoolean();
                }
            }
        }
        return NOptional.ofNamedEmpty("boolean from " + element);
    }


    public NOptional<String> asString() {
        if (element instanceof String) {
            return NOptional.of((String) element);
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case STRING: {
                    return NOptional.of(te.toStr().raw());
                }
                case NAME: {
                    return NOptional.of(te.toName().getName());
                }
            }
        }
        return NOptional.ofNamedEmpty("string from " + element);
    }


    public NOptional<int[]> asIntArray() {
        if (element instanceof int[]) {
            return NOptional.of((int[]) element);
        }
        if (element instanceof TsonElement[]) {
            TsonElement[] arr = (TsonElement[]) element;
            int[] aa = new int[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Integer> d = ObjEx.of(arr[i]).asInt();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("int[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case ARRAY: {
                    return ObjEx.of(te.toArray().all().toArray(new TsonElement[0])).asIntArray();
                }
                case OBJECT: {
                    return ObjEx.of(te.toObject().all().toArray(new TsonElement[0])).asIntArray();
                }
                case UPLET: {
                    return ObjEx.of(te.toUplet().all().toArray(new TsonElement[0])).asIntArray();
                }
            }
        }
        if (element instanceof Int2) {
            return NOptional.of(new int[]{((Int2) element).getY(), ((Int2) element).getY()});
        }
        return NOptional.ofNamedEmpty("int[] from " + element);
    }

    public NOptional<double[]> asDoubleArrayOrDouble() {
        NOptional<double[]> a = asDoubleArray();
        if (a.isPresent()) {
            return a;
        }
        NOptional<Double> b = asDouble();
        if (b.isPresent()) {
            return NOptional.of(new double[]{b.get()});
        }
        return a;
    }

    public NOptional<float[]> asFloatArrayOrFloat() {
        NOptional<float[]> a = asFloatArray();
        if (a.isPresent()) {
            return a;
        }
        NOptional<Float> b = asFloat();
        if (b.isPresent()) {
            return NOptional.of(new float[]{b.get()});
        }
        return a;
    }

    public NOptional<float[]> asFloatArray() {
        return asDoubleArray().map(x -> {
            float[] ff = new float[x.length];
            for (int i = 0; i < x.length; i++) {
                ff[i] = (float) x[i];
            }
            return ff;
        });
    }

    public NOptional<double[]> asDoubleArray() {
        if (element instanceof double[]) {
            return NOptional.of((double[]) element);
        }
        if (element instanceof TsonElement[]) {
            TsonElement[] arr = (TsonElement[]) element;
            double[] aa = new double[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Double> d = ObjEx.of(arr[i]).asDouble();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("double[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case ARRAY: {
                    return ObjEx.of(te.toArray().all().toArray(new TsonElement[0])).asDoubleArray();
                }
                case OBJECT: {
                    return ObjEx.of(te.toObject().all().toArray(new TsonElement[0])).asDoubleArray();
                }
                case UPLET: {
                    return ObjEx.of(te.toUplet().all().toArray(new TsonElement[0])).asDoubleArray();
                }
            }
        }
        if (element instanceof Double2) {
            return NOptional.of(new double[]{((Double2) element).getX(), ((Double2) element).getY()});
        }
        if (element instanceof HPoint2D) {
            return NOptional.of(new double[]{((HPoint2D) element).getY(), ((HPoint2D) element).getY()});
        }
        if (element instanceof HPoint3D) {
            return NOptional.of(new double[]{((HPoint3D) element).getY(), ((HPoint3D) element).getY(), ((HPoint3D) element).getZ()});
        }
        if (element instanceof Double4) {
            Double4 d = (Double4) element;
            return NOptional.of(new double[]{
                    d.getX1(),
                    d.getX2(),
                    d.getX3(),
                    d.getX4(),
            });
        }
        return NOptional.ofNamedEmpty("double[] from " + element);
    }

    public NOptional<String[]> asStringArrayOrString() {
        NOptional<String[]> a = asStringArray();
        if (a.isPresent()) {
            return a;
        }
        NOptional<String> b = asString();
        if (b.isPresent()) {
            return NOptional.of(new String[]{b.get()});
        }
        return a;
    }

    public NOptional<Object[]> asObjectArray() {
        if (element instanceof Object[]) {
            return NOptional.of((Object[]) element);
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case ARRAY: {
                    return NOptional.of(te.toArray().all().toArray(new TsonElement[0]));
                }
                case OBJECT: {
                    return NOptional.of(te.toObject().all().toArray(new TsonElement[0]));
                }
                case UPLET: {
                    return NOptional.of(te.toUplet().all().toArray(new TsonElement[0]));
                }
            }
        }
        return NOptional.ofNamedEmpty("Object[] from " + element);
    }

    public NOptional<Color[]> asColorArray() {
        NOptional<Object[]> o = asObjectArray();
        if(o.isPresent()){
            List<Color> cc=new ArrayList<>();
            for (Object oi : o.get()) {
                NOptional<Color> y = ObjEx.of(oi).parseColor(null);
                if(y.isPresent()){
                    cc.add(y.get());
                }else{
                    return NOptional.ofNamedEmpty("Object[] from " + element);
                }
            }
            return NOptional.of(cc.toArray(new Color[0]));
        }
        return NOptional.ofNamedEmpty("Object[] from " + element);
    }

    public NOptional<String[]> asStringArray() {
        if (element instanceof String[]) {
            return NOptional.of((String[]) element);
        }
        if (element instanceof TsonElement[]) {
            TsonElement[] arr = (TsonElement[]) element;
            String[] aa = new String[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<String> d = ObjEx.of(arr[i]).asString();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("double[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case ARRAY: {
                    return ObjEx.of(te.toArray().all().toArray(new TsonElement[0])).asStringArray();
                }
                case OBJECT: {
                    return ObjEx.of(te.toObject().all().toArray(new TsonElement[0])).asStringArray();
                }
                case UPLET: {
                    return ObjEx.of(te.toUplet().all().toArray(new TsonElement[0])).asStringArray();
                }
            }
        }
        return NOptional.ofNamedEmpty("String[] from " + element);
    }

    public NOptional<boolean[]> asBooleanArray() {
        if (element instanceof boolean[]) {
            return NOptional.of((boolean[]) element);
        }
        if (element instanceof TsonElement[]) {
            TsonElement[] arr = (TsonElement[]) element;
            boolean[] aa = new boolean[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Boolean> d = ObjEx.of(arr[i]).asBoolean();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("double[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case ARRAY: {
                    return ObjEx.of(te.toArray().all().toArray(new TsonElement[0])).asBooleanArray();
                }
                case OBJECT: {
                    return ObjEx.of(te.toObject().all().toArray(new TsonElement[0])).asBooleanArray();
                }
                case UPLET: {
                    return ObjEx.of(te.toUplet().all().toArray(new TsonElement[0])).asBooleanArray();
                }
            }
        }
        return NOptional.ofNamedEmpty("boolean[] from " + element);
    }

    public NOptional<Int2> asInt2() {
        if (element instanceof Int2) {
            return NOptional.of((Int2) element);
        }
        NOptional<int[]> a = asIntArray();
        if (a.isPresent()) {
            int[] g = a.get();
            if (g.length == 2) {
                return NOptional.of(new Int2(g[0], g[1]));
            }
        }
        return NOptional.ofNamedEmpty("int2 from " + element);
    }

    public NOptional<Double2> asDouble2OrHAlign() {
        NOptional<Double2> p = asDouble2();
        if (p.isPresent()) {
            return p;
        }
        if (element instanceof HAlign) {
            return ((HAlign) element).toPosition();
        }
        NOptional<String> k = asString();
        if (k.isPresent()) {
            return HAlign.parse(k.get()).flatMap(x -> x.toPosition());
        }
        return NOptional.ofNamedEmpty("Double from " + element);
    }

    public NOptional<Padding> asPadding() {
        if (element instanceof Padding) {
            return NOptional.of((Padding) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            switch (dd.length) {
                case 1: {
                    return NOptional.of(Padding.of(dd[0]));
                }
                case 2: {
                    return NOptional.of(Padding.of(dd[0], dd[1]));
                }
                case 3: {
                    return NOptional.of(Padding.of(dd[0], dd[1], dd[2]));
                }
                case 4: {
                    return NOptional.of(Padding.of(dd[0], dd[1], dd[2], dd[3]));
                }
            }
        }
        return NOptional.ofNamedEmpty("Padding from " + element);
    }

    public NOptional<Rotation> asRotation() {
        if (element instanceof Rotation) {
            return NOptional.of((Rotation) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            switch (dd.length) {
                case 1: {
                    return NOptional.of(new Rotation(dd[0], 50, 50));
                }
                case 3: {
                    return NOptional.of(new Rotation(dd[0], dd[1], dd[2]));
                }
            }
        }
        NOptional<Double> dd = asDouble();
        if (dd.isPresent()) {
            return NOptional.of(new Rotation(dd.get(), 50, 50));
        }
        return NOptional.ofNamedEmpty("Rotation from " + element);
    }

    public NOptional<Double4> asDouble4() {
        if (element instanceof Double4) {
            return NOptional.of((Double4) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 4) {
                return NOptional.of(new Double4(dd[0], dd[1], dd[2], dd[3]));
            }
        }
        return NOptional.ofNamedEmpty("Double4 from " + element);
    }

    public NOptional<Double2> asDouble2() {
        if (element instanceof Double2) {
            return NOptional.of((Double2) element);
        }
        if (element instanceof HPoint2D) {
            HPoint2D p=(HPoint2D) element;
            return NOptional.of(new Double2(p.x,p.y));
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 2) {
                return NOptional.of(new Double2(dd[0], dd[1]));
            }
        }
        return NOptional.ofNamedEmpty("Double2 from " + element);
    }

    public NOptional<Double3> asDouble3() {
        if (element instanceof Double3) {
            return NOptional.of((Double3) element);
        }
        if (element instanceof HPoint3D) {
            HPoint3D p=(HPoint3D) element;
            return NOptional.of(new Double3(p.x,p.y,p.z));
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 3) {
                return NOptional.of(new Double3(dd[0], dd[1], dd[2]));
            }
        }
        return NOptional.ofNamedEmpty("Double3 from " + element);
    }

    public NOptional<HPoint2D> asHPoint2D() {
        if (element instanceof HPoint2D) {
            return NOptional.of((HPoint2D) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 2) {
                return NOptional.of(new HPoint2D(dd[0], dd[1]));
            }
        }
        return NOptional.ofNamedEmpty("HPoint2D from " + element);
    }

    public NOptional<HArrayHead> asHArrayHead() {
        if (element instanceof HArrayHead) {
            return NOptional.of((HArrayHead) element);
        }
        NOptional<String> s = asString();
        if(s.isPresent()){
            String v = s.get().trim();
            if(v.isEmpty()){
                return NOptional.of(HArrayHead.NONE);
            }
            try{
                HArrayHead y = HArrayHead.valueOf(NNameFormat.CONST_NAME.format(v));
                return NOptional.of(y);
            }catch (Exception e){
                //
            }
        }
        return NOptional.ofNamedError("HArrayHead from " + element);
    }

    public NOptional<HPoint3D> asHPoint3D() {
        if (element instanceof HPoint3D) {
            return NOptional.of((HPoint3D) element);
        }
        NOptional<double[]> d = asDoubleArray();
        if (d.isPresent()) {
            double[] dd = d.get();
            if (dd.length == 2) {
                return NOptional.of(new HPoint3D(dd[0], dd[1], dd[2]));
            }
        }
        return NOptional.ofNamedEmpty("HPoint3D from " + element);
    }


    public <T> NOptional<T> as(Class<T> type) {
        switch (type.getName()) {
            case "java.lang.String": {
                return (NOptional<T>) asString();
            }
            case "boolean": {
                return (NOptional<T>) asBoolean();
            }
            case "double": {
                return (NOptional<T>) asDouble();
            }
            case "int": {
                return (NOptional<T>) asInt();
            }
            case "net.thevpc.halfa.api.model.elem2d.Double2": {
                return (NOptional<T>) asDouble2();
            }
            case "[Lnet.thevpc.halfa.api.model.Double2;": {
                return (NOptional<T>) asDouble2Array();
            }
            case "[Ljava.lang.String;": {
                return (NOptional<T>) asStringArray();
            }
            case "[d": {
                return (NOptional<T>) asDoubleArray();
            }
            default: {
                throw new IllegalArgumentException("unsupported type " + type);
            }
        }
    }

    public NOptional<HPoint2D[]> asHPoint2DArray() {
        NOptional<Double2[]> u = asDouble2Array();
        if(u.isPresent()){
            return NOptional.of(
                    Arrays.stream(u.get()).map(x->new HPoint2D(x.getX(),x.getY())).toArray(HPoint2D[]::new)
            );
        }else{
            return (NOptional) u;
        }
    }

    public NOptional<HPoint3D[]> asHPoint3DArray() {
        NOptional<Double3[]> u = asDouble3Array();
        if(u.isPresent()){
            return NOptional.of(
                    Arrays.stream(u.get()).map(x->new HPoint3D(x.getX(),x.getY(),x.getZ())).toArray(HPoint3D[]::new)
            );
        }else{
            return (NOptional) u;
        }
    }

    public NOptional<Double2[]> asDouble2Array() {
        if (element instanceof Double2[]) {
            return NOptional.of((Double2[]) element);
        }
        if (element instanceof HPoint2D[]) {
            return NOptional.of(
                    Arrays.stream((HPoint2D[])element).map(x->new Double2(x.getX(),x.getY())).toArray(Double2[]::new)
            );
        }
        if (element instanceof TsonElement[]) {
            TsonElement[] arr = (TsonElement[]) element;
            Double2[] aa = new Double2[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Double2> d = ObjEx.of(arr[i]).asDouble2();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("Double2[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case ARRAY: {
                    return ObjEx.of(te.toArray().all().toArray(new TsonElement[0])).asDouble2Array();
                }
                case OBJECT: {
                    return ObjEx.of(te.toObject().all().toArray(new TsonElement[0])).asDouble2Array();
                }
                case UPLET: {
                    return ObjEx.of(te.toUplet().all().toArray(new TsonElement[0])).asDouble2Array();
                }
            }
        }
        return NOptional.ofNamedEmpty("Double2[] from " + element);
    }

    public NOptional<Double3[]> asDouble3Array() {
        if (element instanceof Double3[]) {
            return NOptional.of((Double3[]) element);
        }
        if (element instanceof TsonElement[]) {
            TsonElement[] arr = (TsonElement[]) element;
            Double3[] aa = new Double3[arr.length];
            for (int i = 0; i < aa.length; i++) {
                NOptional<Double3> d = ObjEx.of(arr[i]).asDouble3();
                if (d.isPresent()) {
                    aa[i] = d.get();
                } else {
                    return NOptional.ofNamedEmpty("Double3[] from " + element);
                }
            }
            return NOptional.of(aa);
        }
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case ARRAY: {
                    return ObjEx.of(te.toArray().all().toArray(new TsonElement[0])).asDouble3Array();
                }
                case OBJECT: {
                    return ObjEx.of(te.toObject().all().toArray(new TsonElement[0])).asDouble3Array();
                }
                case UPLET: {
                    return ObjEx.of(te.toUplet().all().toArray(new TsonElement[0])).asDouble3Array();
                }
            }
        }
        return NOptional.ofNamedEmpty("Double3[] from " + element);
    }

    public boolean isFunction() {
        if (element instanceof TsonElement) {
            TsonElement te = (TsonElement) element;
            switch (te.type()) {
                case FUNCTION: {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasName() {
        return !NBlankable.isBlank(name());
    }

    public static class SimplePair {
        private String name;
        private TsonElement key;
        private ObjEx value;

        public SimplePair(String name, TsonElement key, ObjEx value) {
            this.name = name;
            this.key = key;
            this.value = value;
        }

        public String getNameId() {
            return HUtils.uid(getName());
        }

        public String getName() {
            return name;
        }

        public TsonElement getKey() {
            return key;
        }

        public ObjEx getValue() {
            return value;
        }
    }

    public Object raw() {
        return element;
    }
}

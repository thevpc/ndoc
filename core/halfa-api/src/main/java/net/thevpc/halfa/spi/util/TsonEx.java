//package net.thevpc.halfa.spi.util;
//
//import net.thevpc.halfa.api.model.elem2d.Double2;
//import net.thevpc.halfa.api.model.elem2d.Int2;
//import net.thevpc.nuts.util.NLiteral;
//import net.thevpc.nuts.util.NNameFormat;
//import net.thevpc.nuts.util.NOptional;
//import net.thevpc.tson.*;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ObjEx {
//    private TsonElement element;
//    private String name;
//    private List<TsonElement> args = new ArrayList<>();
//    private List<TsonElement> children = new ArrayList<>();
//
//    private boolean parsedChildren;
//
//    public ObjEx(TsonElement element) {
//        this.element = element;
//    }
//
//    private void _parsedChildren() {
//        if (!parsedChildren) {
//            parsedChildren = true;
//            switch (element.type()) {
//                case FUNCTION: {
//                    TsonFunction item = element.toFunction();
//                    name = HUtils.uid(item.name());
//                    ;
//                    args.addAll(item.all());
//                    break;
//                }
//                case OBJECT: {
//                    TsonObject item = element.toObject();
//                    TsonElementHeader header = item.getHeader();
//                    if (header != null) {
//                        name = header.name();
//                        args.addAll(header.all());
//                    }
//                    children.addAll(item.all());
//                    break;
//                }
//                case ARRAY: {
//                    TsonArray item = element.toArray();
//                    TsonElementHeader header = item.getHeader();
//                    if (header != null) {
//                        name = header.name();
//                        args.addAll(header.all());
//                    }
//                    children.addAll(item.all());
//                    break;
//                }
//            }
//
//        }
//    }
//
//    public NOptional<SimplePair> asSimplePair() {
//        switch (element.type()) {
//            case PAIR: {
//                TsonElement key = element.toPair().getKey();
//                NOptional<String> s = new ObjEx(key).asString();
//                if (s.isPresent()) {
//                    return NOptional.of(
//                            new SimplePair(
//                                    s.get(),
//                                    key,
//                                    new ObjEx(element.toPair().getValue())
//                            )
//                    );
//                }
//                break;
//            }
//        }
//        return NOptional.ofNamedEmpty("pair");
//    }
//
//
//    public String name() {
//        _parsedChildren();
//        return name;
//    }
//
//    public List<TsonElement> args() {
//        _parsedChildren();
//        return args;
//    }
//
//    public List<TsonElement> children() {
//        _parsedChildren();
//        return children;
//    }
//
//    public NOptional<Color> parseColor() {
//        switch (element.type()) {
//            case BYTE:
//            case BIG_INT:
//            case SHORT:
//            case LONG:
//            case INT: {
//                return NOptional.of(new Color(element.toInt().getInt()));
//            }
//            case STRING:
//            case NAME: {
//                ObjEx h = new ObjEx(element);
//                String s = HUtils.uid(h.asString().get());
//                if (s.startsWith("#")) {
//                    return NOptional.of(new Color(Integer.parseInt(s.substring(1), 16)));
//                }
//                switch (NNameFormat.LOWER_KEBAB_CASE.format(s.toLowerCase())) {
//                    case "red": {
//                        return NOptional.of(Color.RED);
//                    }
//                    case "blue": {
//                        return NOptional.of(Color.BLUE);
//                    }
//                    case "black": {
//                        return NOptional.of(Color.BLACK);
//                    }
//                    case "white": {
//                        return NOptional.of(Color.WHITE);
//                    }
//                    case "yellow": {
//                        return NOptional.of(Color.YELLOW);
//                    }
//                    case "cyan": {
//                        return NOptional.of(Color.CYAN);
//                    }
//                    case "orange": {
//                        return NOptional.of(Color.ORANGE);
//                    }
//                    case "pink": {
//                        return NOptional.of(Color.PINK);
//                    }
//                    case "dark-gray": {
//                        return NOptional.of(Color.DARK_GRAY);
//                    }
//                    case "light-gray": {
//                        return NOptional.of(Color.LIGHT_GRAY);
//                    }
//                    case "gray": {
//                        return NOptional.of(Color.GRAY);
//                    }
//                    case "green": {
//                        return NOptional.of(Color.GREEN);
//                    }
//                    case "magenta": {
//                        return NOptional.of(Color.MAGENTA);
//                    }
//                }
//                try {
//                    int z = Integer.parseInt(s, 16);
//                    return NOptional.of(new Color(z));
//                } catch (Exception e) {
//
//                }
//                try {
//                    int z = Integer.parseInt(s);
//                    return NOptional.of(new Color(z));
//                } catch (Exception e) {
//
//                }
//                break;
//            }
//        }
//        return NOptional.ofNamedEmpty("color from " + element);
//    }
//
//    public NOptional<Int2> asInt2() {
//        switch (element.type()) {
//            case ARRAY: {
//                TsonArray arr = element.toArray();
//                if (arr.size() == 2) {
//                    TsonElement a = arr.get(0);
//                    TsonElement b = arr.get(1);
//                    int ad = a.toInt().getValue();
//                    int bd = b.toInt().getValue();
//                    return NOptional.of(new Int2(ad, bd));
//                }
//                break;
//            }
//            case OBJECT: {
//                TsonObject arr = element.toObject();
//                if (arr.size() == 2) {
//                    List<TsonElement> all = arr.all();
//                    TsonElement a = all.get(0);
//                    TsonElement b = all.get(1);
//                    if (a.type().isNumber()) {
//                        int ad = a.toInt().getValue();
//                        int bd = b.toInt().getValue();
//                        return NOptional.of(new Int2(ad, bd));
//                    }
//                }
//                break;
//            }
//            case UPLET: {
//                TsonUplet arr = element.toUplet();
//                if (arr.size() == 2) {
//                    List<TsonElement> all = arr.all();
//                    TsonElement a = all.get(0);
//                    TsonElement b = all.get(1);
//                    if (a.type().isNumber()) {
//                        int ad = a.toInt().getValue();
//                        int bd = b.toInt().getValue();
//                        return NOptional.of(new Int2(ad, bd));
//                    }
//                }
//                break;
//            }
//        }
//        return NOptional.ofNamedEmpty("int from " + element);
//    }
//
//    public NOptional<Double2> asDouble2() {
//        switch (element.type()) {
//            case ARRAY: {
//                TsonArray arr = element.toArray();
//                if (arr.size() == 2) {
//                    TsonElement a = arr.get(0);
//                    TsonElement b = arr.get(1);
//                    double ad = a.toDouble().getValue();
//                    double bd = b.toDouble().getValue();
//                    return NOptional.of(new Double2(ad, bd));
//                }
//                break;
//            }
//            case OBJECT: {
//                TsonObject arr = element.toObject();
//                if (arr.size() == 2) {
//                    List<TsonElement> all = arr.all();
//                    TsonElement a = all.get(0);
//                    TsonElement b = all.get(1);
//                    if (a.type().isNumber()) {
//                        double ad = a.toDouble().getValue();
//                        double bd = b.toDouble().getValue();
//                        return NOptional.of(new Double2(ad, bd));
//                    }
//                }
//                break;
//            }
//            case UPLET: {
//                TsonUplet arr = element.toUplet();
//                if (arr.size() == 2) {
//                    List<TsonElement> all = arr.all();
//                    TsonElement a = all.get(0);
//                    TsonElement b = all.get(1);
//                    if (a.type().isNumber()) {
//                        double ad = a.toDouble().getValue();
//                        double bd = b.toDouble().getValue();
//                        return NOptional.of(new Double2(ad, bd));
//                    }
//                }
//                break;
//            }
//        }
//        return NOptional.ofNamedEmpty("int from " + element);
//    }
//
//    public NOptional<Integer> asInt() {
//        switch (element.type()) {
//            case INT: {
//                return NOptional.of(element.toInt().getInt());
//            }
//        }
//        return NOptional.ofNamedEmpty("int from " + element);
//    }
//
//    public <T> NOptional<T> as(Class<T> type) {
//        switch (type.getName()) {
//            case "java.lang.String": {
//                return (NOptional<T>) asString();
//            }
//            case "boolean": {
//                return (NOptional<T>) asBoolean();
//            }
//            case "double": {
//                return (NOptional<T>) asDouble();
//            }
//            case "int": {
//                return (NOptional<T>) asInt();
//            }
//            case "net.thevpc.halfa.api.model.elem2d.Double2": {
//                return (NOptional<T>) asDouble2();
//            }
//            case "[Lnet.thevpc.halfa.api.model.Double2;": {
//                return (NOptional<T>) asDouble2Array();
//            }
//            case "[Ljava.lang.String;": {
//                return (NOptional<T>) asStringArray();
//            }
//            case "[d": {
//                return (NOptional<T>) asDoubleArray();
//            }
//            default: {
//                throw new IllegalArgumentException("unsupported type " + type);
//            }
//        }
//    }
//
//    public NOptional<Double> asDouble() {
//        switch (element.type()) {
//            case INT:
//            case LONG:
//            case DOUBLE:
//            case SHORT:
//            case BYTE: {
//                return NOptional.of(element.toDouble().getDouble());
//            }
//        }
//        return NOptional.ofNamedEmpty("double from " + element);
//    }
//
//    public NOptional<String[]> asStringArray() {
//        switch (element.type()) {
//            case STRING: {
//                return NOptional.of(new String[]{element.toStr().getString()});
//            }
//            case NAME: {
//                return NOptional.of(new String[]{element.toName().getString()});
//            }
//            case ARRAY: {
//                List<String> ok = new ArrayList<>();
//                for (TsonElement e : element.toArray().all()) {
//                    NOptional<String> iu = new ObjEx(e).asString();
//                    if (iu.isPresent()) {
//                        ok.add(iu.get());
//                    } else {
//                        return NOptional.ofNamedEmpty("valid array of strings : " + e + " of " + element);
//                    }
//                }
//                return NOptional.of(ok.toArray(new String[0]));
//            }
//        }
//        return NOptional.ofNamedEmpty("string or name");
//    }
//
//    public NOptional<double[]> asDoubleArray() {
//        switch (element.type()) {
//            case ARRAY: {
//                List<Double> ok = new ArrayList<>();
//                for (TsonElement e : element.toArray().all()) {
//                    NOptional<Double> iu = new ObjEx(e).asDouble();
//                    if (iu.isPresent()) {
//                        ok.add(iu.get());
//                    } else {
//                        return NOptional.ofNamedEmpty("valid array of double : " + e + " of " + element);
//                    }
//                }
//
//                return NOptional.of(ok.stream().mapToDouble(x -> x).toArray());
//            }
//        }
//        return NOptional.ofNamedEmpty("string or name");
//    }
//
//    public NOptional<Double2[]> asDouble2Array() {
//        switch (element.type()) {
//            case ARRAY: {
//                List<Double2> ok = new ArrayList<>();
//                for (TsonElement e : element.toArray().all()) {
//                    NOptional<Double2> iu = new ObjEx(e).asDouble2();
//                    if (iu.isPresent()) {
//                        ok.add(iu.get());
//                    } else {
//                        return NOptional.ofNamedEmpty("valid array of Double2 : " + e + " of " + element);
//                    }
//                }
//
//                return NOptional.of(ok.toArray(new Double2[0]));
//            }
//        }
//        return NOptional.ofNamedEmpty("Double2 Array");
//    }
//
//    public static class SimplePair{
//        private String name;
//        private TsonElement key;
//        private ObjEx value;
//
//        public SimplePair(String name, TsonElement key, ObjEx value) {
//            this.name = name;
//            this.key = key;
//            this.value = value;
//        }
//
//        public String getNameId() {
//            return HUtils.uid(getName());
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public TsonElement getKey() {
//            return key;
//        }
//
//        public ObjEx getValue() {
//            return value;
//        }
//    }
//
//    public NOptional<String> asString() {
//        switch (element.type()) {
//            case STRING: {
//                return NOptional.of(element.toStr().raw());
//            }
//            case NAME: {
//                return NOptional.of(element.toName().getString());
//            }
//        }
//        return NOptional.ofNamedEmpty("string or name");
//    }
//
//    public NOptional<Boolean> asBoolean() {
//        switch (element.type()) {
//            case BOOLEAN: {
//                return NOptional.of(element.toBoolean().getBoolean());
//            }
//            case STRING: {
//                return NLiteral.of(element.toStr().getString()).asBoolean();
//            }
//            case NAME: {
//                return NLiteral.of(element.toName().getString()).asBoolean();
//            }
//        }
//        return NOptional.ofNamedEmpty("boolean");
//    }
//}

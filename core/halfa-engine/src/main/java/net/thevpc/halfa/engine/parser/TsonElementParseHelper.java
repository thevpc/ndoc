package net.thevpc.halfa.engine.parser;

import net.thevpc.nuts.util.NLiteral;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonArray;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonObject;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class TsonElementParseHelper {
    private TsonElement element;

    public TsonElementParseHelper(TsonElement element) {
        this.element = element;
    }
    public NOptional<Color> parseColor() {
        switch (element.type()) {
            case BYTE:
            case BIG_INT:
            case SHORT:
            case LONG:
            case INT: {
                return NOptional.of(new Color(element.toInt().getInt()));
            }
            case STRING:
            case NAME: {
                TsonElementParseHelper h = new TsonElementParseHelper(element);
                String s = HParseHelper.uid(h.asStringOrName().get());
                if (s.startsWith("#")) {
                    return NOptional.of(new Color(Integer.parseInt(s.substring(1), 16)));
                }
                switch (s) {
                    case "red": {
                        return NOptional.of(Color.RED);
                    }
                }
                return NOptional.of(new Color(Integer.parseInt(s, 16)));
            }
        }
        return NOptional.ofNamedEmpty("color from " + element);
    }

    public NOptional<Point2D.Double> asPoint2D() {
        switch (element.type()) {
            case ARRAY: {
                TsonArray arr = element.toArray();
                if (arr.size() == 2) {
                    TsonElement a = arr.get(0);
                    TsonElement b = arr.get(1);
                    double ad = a.toDouble().getValue();
                    double bd = b.toDouble().getValue();
                    return NOptional.of(new Point2D.Double(ad, bd));
                }
                break;
            }
            case OBJECT: {
                TsonObject arr = element.toObject();
                if (arr.size() == 2) {
                    List<TsonElement> all = arr.all();
                    TsonElement a = all.get(0);
                    TsonElement b = all.get(1);
                    if (a.type().isNumber()) {
                        double ad = a.toDouble().getValue();
                        double bd = b.toDouble().getValue();
                        return NOptional.of(new Point2D.Double(ad, bd));
                    }
                }
                break;
            }
        }
        return NOptional.ofNamedEmpty("int from " + element);
    }

    public NOptional<Integer> asInt() {
        switch (element.type()) {
            case INT: {
                return NOptional.of(element.toInt().getInt());
            }
        }
        return NOptional.ofNamedEmpty("int from " + element);
    }

    public NOptional<Double> asDouble() {
        switch (element.type()) {
            case INT:
            case LONG:
            case DOUBLE:
            case SHORT:
            case BYTE: {
                return NOptional.of(element.toDouble().getDouble());
            }
        }
        return NOptional.ofNamedEmpty("double from " + element);
    }

    public NOptional<String[]> asStringOrNameArray() {
        switch (element.type()) {
            case STRING: {
                return NOptional.of(new String[]{element.toStr().getString()});
            }
            case NAME: {
                return NOptional.of(new String[]{element.toName().getString()});
            }
            case ARRAY: {
                List<String> ok = new ArrayList<>();
                for (TsonElement e : element.toArray().all()) {
                    NOptional<String> iu = new TsonElementParseHelper(e).asStringOrName();
                    if (iu.isPresent()) {
                        ok.add(iu.get());
                    } else {
                        return NOptional.ofNamedEmpty("valid array of strings : " + e + " of " + element);
                    }
                }
                return NOptional.of(ok.toArray(new String[0]));
            }
        }
        return NOptional.ofNamedEmpty("string or name");
    }

    public NOptional<double[]> asDoubleArray() {
        switch (element.type()) {
            case ARRAY: {
                List<Double> ok = new ArrayList<>();
                for (TsonElement e : element.toArray().all()) {
                    NOptional<Double> iu = new TsonElementParseHelper(e).asDouble();
                    if (iu.isPresent()) {
                        ok.add(iu.get());
                    } else {
                        return NOptional.ofNamedEmpty("valid array of double : " + e + " of " + element);
                    }
                }

                return NOptional.of(ok.stream().mapToDouble(x -> x).toArray());
            }
        }
        return NOptional.ofNamedEmpty("string or name");
    }

    public NOptional<String> asStringOrName() {
        switch (element.type()) {
            case STRING: {
                return NOptional.of(element.toStr().getString());
            }
            case NAME: {
                return NOptional.of(element.toName().getString());
            }
        }
        return NOptional.ofNamedEmpty("string or name");
    }

    public NOptional<Boolean> asBoolean() {
        switch (element.type()) {
            case BOOLEAN: {
                return NOptional.of(element.toBoolean().getBoolean());
            }
            case STRING: {
                return NLiteral.of(element.toStr().getString()).asBoolean();
            }
            case NAME: {
                return NLiteral.of(element.toName().getString()).asBoolean();
            }
        }
        return NOptional.ofNamedEmpty("boolean");
    }
}

package net.thevpc.halfa.spi.util;

import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElementBase;
import net.thevpc.tson.TsonNumber;

public class HSizeRef {
    private double rootWidth;
    private double rootHeight;

    private double parentWidth;
    private double parentHeight;

    public HSizeRef(double parentWidth, double parentHeight) {
        this(parentWidth, parentHeight, parentWidth, parentHeight);
    }

    public HSizeRef(double parentWidth, double parentHeight, double rootWidth, double rootHeight) {
        this.rootWidth = rootWidth;
        this.rootHeight = rootHeight;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
    }

    public double getRootWidth() {
        return rootWidth;
    }

    public double getRootHeight() {
        return rootHeight;
    }

    public double getParentWidth() {
        return parentWidth;
    }

    public double getParentHeight() {
        return parentHeight;
    }


    public NOptional<Double> x(Object o) {
        return xy(o, parentWidth, rootWidth);
    }

    public NOptional<Double> y(Object o) {
        return xy(o, parentHeight, rootHeight);
    }

    private NOptional<Double> xy(Object o, double pw, double rw) {
        if (o == null) {
            return NOptional.ofNamedEmpty("size");
        }
        if (o instanceof Number) {
            o = Tson.of(((Number) o).doubleValue());
        }
        if (o instanceof TsonElementBase) {
            TsonElementBase b = (TsonElementBase) o;
            if (b.build().isNumber()) {
                TsonNumber n = b.build().toNumber();
                String u = n.unit();
                if (u == null) {
                    u = "";
                } else {
                    u = u.toLowerCase().trim();
                }
                switch (u) {
                    case "%g": {
                        double aDouble = n.doubleValue();
                        return NOptional.of(aDouble / 100 * rw);
                    }
                    case "px": {
                        double aDouble = n.doubleValue();
                        return NOptional.of(aDouble);
                    }
                    case "rem": {
                        double aDouble = n.doubleValue();
                        return NOptional.of(aDouble / 18);
                    }
                    case "":
                    case "%":
                    default: {
                        double aDouble = n.doubleValue();
                        return NOptional.of(aDouble / 100 * pw);
                    }
                }
            }
        }
        return NOptional.ofNamedEmpty("size");
    }

    public Double2 apply(Double2 size) {
        return new Double2(x(Tson.of(size.getX())).get(),y(Tson.of(size.getX())).get());
    }
}

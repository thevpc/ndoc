package net.thevpc.ntexup.api.util;

import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.elem2d.NTxSize;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NNumberElement;
import net.thevpc.nuts.util.NOptional;

public class NTxSizeRef {
    private double rootWidth;
    private double rootHeight;

    private double parentWidth;
    private double parentHeight;

    public NTxSizeRef(double parentWidth, double parentHeight) {
        this(parentWidth, parentHeight, parentWidth, parentHeight);
    }

    public NTxSizeRef(double parentWidth, double parentHeight, double rootWidth, double rootHeight) {
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
        return xy(o, parentWidth, rootWidth,true);
    }

    public NOptional<Double> y(Object o) {
        return xy(o, parentHeight, rootHeight,false);
    }

    private NOptional<Double> xy(Object o, double pw, double rw,boolean isx) {
        if (o == null) {
            return NOptional.ofNamedEmpty("size");
        }
        if (o instanceof Number) {
            o = NElement.ofDouble(((Number) o).doubleValue());
        }
        if (o instanceof NTxSize) {
            NTxSize ss = (NTxSize) o;
            switch (ss.type()) {
                case REM:
                    return NOptional.of(ss.value() / 18);
                case PX:
                    return NOptional.of(ss.value());
                case PAGE:
                    return NOptional.of(ss.value() / 100 * rw);
                case PARENT:
                    return NOptional.of(ss.value() / 100 * pw);
                case BOUNDS:{
                    if(isx){
                        return NOptional.of(ss.value());
                    }
                    return NOptional.of(ss.height());
                }
            }
        }
        if (o instanceof NElement) {
            NElement b = (NElement) o;
            if (b.isNumber()) {
                Number n = b.asNumberValue().get();
                String u = ((NNumberElement) b).numberSuffix();
                if (u == null) {
                    u = "";
                } else {
                    u = u.toLowerCase().trim();
                }
                switch (u) {
                    case "%p":
                    case "%P": {
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

    public NTxDouble2 apply(NTxDouble2 size) {
        return new NTxDouble2(x(NElement.ofDouble(size.getX())).get(), y(NElement.ofDouble(size.getX())).get());
    }
}

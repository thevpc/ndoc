package net.thevpc.halfa.engine.renderer.screen.common.strokes;

import net.thevpc.halfa.engine.renderer.screen.common.shapes.ShapeFactory;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StrokeFactory {


    public static Stroke createStroke(String name, TsonElement e) {
        ObjEx o = ObjEx.of(e);
        switch (HUtils.uid(name)) {
            case "basic": {
                return createBasic(o);
            }
            case "wobble": {
                return WobbleStroke.of(e);
            }
            case "sloppy": {
                return SloppyStroke.of(e);
            }
            case "zigzag": {
                return ZigzagStroke.of(e);
            }
            case "add": {
                return CompoundStroke.of(e, CompoundStroke.Op.ADD);
            }
            case "sub": {
                return CompoundStroke.of(e, CompoundStroke.Op.SUBTRACT);
            }
            case "diff": {
                return CompoundStroke.of(e, CompoundStroke.Op.DIFFERENCE);
            }
            case "inter": {
                return CompoundStroke.of(e, CompoundStroke.Op.INTERSECT);
            }
            case "compose": {
                return CompositeStroke.of(e);
            }
            case "shaped": {
                return createShaped(o);
            }
        }
        return createBasic(o);
    }

    public static Stroke createStroke(TsonElement e) {
        ObjEx o = ObjEx.of(e);
        if (o.name() != null) {
            return createStroke(o.name(), e);
        }
        switch (e.type()) {
            case ARRAY: {
                return CompositeStroke.of(e);
            }
            case UPLET: {
                return createBasic(o);
            }
            case NAME:
            case STRING: {
                return createStroke(o.asStringOrName().get(), e);
            }
        }
        {
            NOptional<Float> d = o.asFloat();
            if (d.isPresent()) {
                return new BasicStroke(d.get());
            }
        }
        return new BasicStroke();
    }

    private static Stroke createShaped(ObjEx o) {
        List<Shape> base = new ArrayList<>();
        double advance = 15;
        for (TsonElement arg : o.args()) {
            if (
                    arg.type() == TsonElementType.UPLET
                            || arg.type() == TsonElementType.FUNCTION
                            || arg.type() == TsonElementType.ARRAY
                            || arg.type() == TsonElementType.OBJECT
            ) {
                base.add(ShapeFactory.createShape(arg));
            } else {
                NOptional<ObjEx.SimplePair> sp = o.asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair ke = sp.get();
                    switch (HUtils.uid(ke.getName())) {
                        case "advance": {
                            advance = ke.getValue().asDouble().orElse(advance);
                            break;
                        }
                    }
                }
            }
        }
        return new ShapeStroke(base.toArray(new Shape[0]), (float) advance);
    }

    public static BasicStroke createBasic(ObjEx o) {
        double width = 1;
        int cap = BasicStroke.CAP_SQUARE;
        int join = BasicStroke.JOIN_MITER;
        double miterLimit = 10;
        float[] dash = null;
        double dashPhase = 0;
        for (TsonElement ee : o.argsOrBody()) {
            ObjEx objEx = ObjEx.of(ee);
            NOptional<ObjEx.SimplePair> sp = objEx.asSimplePair();
            if (sp.isPresent()) {
                ObjEx.SimplePair ke = sp.get();
                switch (ke.getNameId()) {
                    case "width": {
                        width = ke.getValue().asDouble().orElse(width);
                        break;
                    }
                    case "dash-phase": {
                        dashPhase = ke.getValue().asDouble().orElse(dashPhase);
                        break;
                    }
                    case "miter-limit": {
                        miterLimit = ke.getValue().asDouble().orElse(miterLimit);
                        break;
                    }
                    case "dash": {
                        dash = ke.getValue().asFloatArrayOrFloat().orElse(null);
                        break;
                    }
                    case "cap": {
                        String s = ke.getValue().asStringOrName().orElse(null);
                        if (s != null) {
                            switch (HUtils.uid(s)) {
                                case "square": {
                                    cap = BasicStroke.CAP_SQUARE;
                                    break;
                                }
                                case "round": {
                                    cap = BasicStroke.CAP_ROUND;
                                    break;
                                }
                                case "butt": {
                                    cap = BasicStroke.CAP_BUTT;
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case "join": {
                        String s = ke.getValue().asStringOrName().orElse(null);
                        if (s != null) {
                            switch (HUtils.uid(s)) {
                                case "miter": {
                                    join = BasicStroke.JOIN_MITER;
                                    break;
                                }
                                case "bevel": {
                                    join = BasicStroke.JOIN_BEVEL;
                                    break;
                                }
                                case "round": {
                                    join = BasicStroke.JOIN_ROUND;
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        NOptional<Double> w = ke.getValue().asDouble();
                        if (w.isPresent()) {
                            dashPhase = w.get();
                        }
                        break;
                    }
                }
            } else {
                NOptional<Double> w = objEx.asDouble();
                if (w.isPresent()) {
                    width = w.get();
                } else {
                    NOptional<String> n = objEx.asName();
                    if (n.isPresent()) {
                        switch (n.get()) {
                            case "square": {
                                cap = BasicStroke.CAP_SQUARE;
                                break;
                            }
                            case "round": {
                                cap = BasicStroke.CAP_ROUND;
                                join = BasicStroke.JOIN_ROUND;
                                break;
                            }
                            case "butt": {
                                cap = BasicStroke.CAP_BUTT;
                                break;
                            }
                            case "miter": {
                                join = BasicStroke.JOIN_MITER;
                                break;
                            }
                            case "bevel": {
                                join = BasicStroke.JOIN_BEVEL;
                                break;
                            }
                            case "dash": {
                                if (dash == null) {
                                    dash = new float[]{8};
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (dash != null && dash.length == 0) {
            dash = null;
        }
        if (dash != null) {
            if (dashPhase == 0) {
                dashPhase = 10;
            }
        } else {
            dashPhase = 0;
        }
        return new BasicStroke(
                (float) width,
                cap,
                join,
                (float) miterLimit,
                dash,
                (float) dashPhase
        );
    }
}

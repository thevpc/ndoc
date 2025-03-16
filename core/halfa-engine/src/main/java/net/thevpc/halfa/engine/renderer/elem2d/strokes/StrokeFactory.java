package net.thevpc.halfa.engine.renderer.elem2d.strokes;

import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StrokeFactory {
    public static final float[] DASH_LARGE = new float[]{8};
    public static final float[] DASH_NORMAL = new float[]{5};
    public static final float[] DASH_SMALL = new float[]{3};

    public static Stroke createStroke(String name, TsonElement e, HGraphics g) {
        ObjEx o = ObjEx.of(e);
        switch (net.thevpc.halfa.api.util.HUtils.uid(name)) {
            case "basic":
            case "simple":
            case "regular":
            case "straight": {
                return createBasic(o);
            }
            case "wobble": {
                return WobbleStroke.of(e);
            }
            case "sloppy": {
                return SloppyStroke.of(e, g);
            }
            case "zigzag": {
                return ZigzagStroke.of(e, g);
            }
            case "add": {
                return CompoundStroke.of(e, CompoundStroke.Op.ADD, g);
            }
            case "sub": {
                return CompoundStroke.of(e, CompoundStroke.Op.SUBTRACT, g);
            }
            case "diff": {
                return CompoundStroke.of(e, CompoundStroke.Op.DIFFERENCE, g);
            }
            case "inter": {
                return CompoundStroke.of(e, CompoundStroke.Op.INTERSECT, g);
            }
            case "compose": {
                return CompositeStroke.of(e, g);
            }
            case "shaped": {
                return createShaped(o, g);
            }
        }
        return createBasic(o);
    }

    private static Stroke createShaped(ObjEx o, HGraphics g) {
        List<Shape> base = new ArrayList<>();
        double advance = 15;
        for (TsonElement arg : o.args()) {
            if (
                    arg.type() == TsonElementType.UPLET
                            || arg.type() == TsonElementType.ARRAY
                            || arg.type() == TsonElementType.OBJECT
            ) {
                base.add(g.createShape(arg));
            } else {
                NOptional<ObjEx.SimplePair> sp = o.asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair ke = sp.get();
                    switch (net.thevpc.halfa.api.util.HUtils.uid(ke.getName())) {
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
        double width = .5;
        int cap = BasicStroke.CAP_SQUARE;
        int join = BasicStroke.JOIN_MITER;
        double miterLimit = 10;
        float[] dash = null;
        double dashPhase = 0;
        if (o.asDouble().isPresent()) {
            width = o.asDouble().get();
        } else if (o.asName().isPresent()) {
            switch (net.thevpc.halfa.api.util.HUtils.uid(o.asName().get())) {
                case "dash":
                case "dashed": {
                    dash = DASH_NORMAL;
                    break;
                }
            }
        } else {
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
                                switch (net.thevpc.halfa.api.util.HUtils.uid(s)) {
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
                                case "dash":
                                case "dashed": {
                                    if (dash == null) {
                                        dash = DASH_NORMAL;
                                    }
                                    break;
                                }
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

package net.thevpc.halfa.engine.renderer.screen.common.strokes;

import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementType;

import java.awt.*;
import java.awt.geom.Area;

/**
 * gpl2
 * http://www.jhlabs.com/java/java2d/strokes/CompoundStroke.java
 */
public class CompoundStroke implements Stroke {
    public static enum Op {
        ADD,
        SUBTRACT,
        INTERSECT,
        DIFFERENCE
    }

    private Stroke stroke1, stroke2;
    private Op operation;

    public static Stroke of(TsonElement ee, CompoundStroke.Op op) {
        ObjEx o = ObjEx.of(ee);
        Stroke base1 = null;
        Stroke base2 = null;
        for (TsonElement arg : o.args()) {
            if (
                    arg.type() == TsonElementType.UPLET
                            || arg.type() == TsonElementType.FUNCTION
                            || arg.type() == TsonElementType.ARRAY
                            || arg.type() == TsonElementType.OBJECT
            ) {
                Stroke stroke = StrokeFactory.createStroke(arg);
                if (base1 == null) {
                    base1 = stroke;
                } else if (base2 == null) {
                    base2 = stroke;
                }
            }
        }
        if (base1 == null && base2 == null) {
            return StrokeFactory.createBasic(ObjEx.of(null));
        }
        if (base1 == null) {
            return base2;
        }
        if (base2 == null) {
            return base1;
        }
        return new CompoundStroke(base1, base2, op);
    }

    public CompoundStroke(Stroke stroke1, Stroke stroke2, Op operation) {
        this.stroke1 = stroke1;
        this.stroke2 = stroke2;
        this.operation = operation;
    }

    public Shape createStrokedShape(Shape shape) {
        Area area1 = new Area(stroke1.createStrokedShape(shape));
        Area area2 = new Area(stroke2.createStrokedShape(shape));
        switch (operation) {
            case ADD:
                area1.add(area2);
                break;
            case SUBTRACT:
                area1.subtract(area2);
                break;
            case INTERSECT:
                area1.intersect(area2);
                break;
            case DIFFERENCE:
                area1.exclusiveOr(area2);
                break;
        }
        return area1;
    }
}
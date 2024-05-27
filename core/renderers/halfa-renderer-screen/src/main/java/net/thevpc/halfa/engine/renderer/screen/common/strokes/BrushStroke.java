package net.thevpc.halfa.engine.renderer.screen.common.strokes;

import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementType;

import java.awt.*;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.Random;

public class BrushStroke implements Stroke{

    private float width;
    private Random random;
    private Stroke base;

    public static Stroke of(TsonElement e) {
        ObjEx o = ObjEx.of(e);
        double width = 5;
        Stroke base=null;
        for (TsonElement arg : o.args()) {
            if (
                    arg.type() == TsonElementType.UPLET
                            || arg.type() == TsonElementType.FUNCTION
                            || arg.type() == TsonElementType.ARRAY
                            || arg.type() == TsonElementType.OBJECT
            ) {
                if (base == null) {
                    base = StrokeFactory.createStroke(arg);
                }
            } else {
                NOptional<ObjEx.SimplePair> sp = o.asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair ke = sp.get();
                    switch (HUtils.uid(ke.getName())) {
                        case "width": {
                            width = ke.getValue().asDouble().orElse(width);
                            break;
                        }
                    }
                }
            }
        }
        return new BrushStroke(
                base,
                (float) width
        );
    }

    public BrushStroke(Stroke base,float width) {
        this.width = width;
        this.random = new Random();
        this.base=base==null?new BasicStroke():base;
    }

    @Override
    public Shape createStrokedShape(Shape shape) {
        GeneralPath result = new GeneralPath();
        PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), 1);
        float points[] = new float[6];
        float moveX = 0, moveY = 0;
        float lastX = 0, lastY = 0;
        float thisX = 0, thisY = 0;
        int type = 0;
        boolean first = false;
        float next = 0;
        int phase = 0;

        float factor = 1;

        while (!it.isDone()) {
            type = it.currentSegment(points);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    moveX = lastX = points[0];
                    moveY = lastY = points[1];
                    result.moveTo(moveX, moveY);
                    first = true;
//                    next = wavelength / 2;
                    break;

                case PathIterator.SEG_CLOSE:
                    points[0] = moveX;
                    points[1] = moveY;
                    // Fall into....

                case PathIterator.SEG_LINETO:
                    thisX = points[0];
                    thisY = points[1];
                    result.lineTo(thisX+random.nextFloat()* 2*width-width, thisY+random.nextFloat()* 2*width-width);
                    result.lineTo(lastX, lastY);
                    result.lineTo(thisX, thisY);
////                    float dx = thisX - lastX;
////                    float dy = thisY - lastY;
////                    float distance = (float) Math.sqrt(dx * dx + dy * dy);
////                    if (distance >= next) {
////                        float r = 1.0f / distance;
////                        float angle = (float) Math.atan2(dy, dx);
////                        while (distance >= next) {
////                            float x = lastX + next * dx * r;
////                            float y = lastY + next * dy * r;
////                            float tx = amplitude * dy * r;
////                            float ty = amplitude * dx * r;
////                            if ((phase & 1) == 0)
////                                result.lineTo(x + amplitude * dy * r, y - amplitude * dx * r);
////                            else
////                                result.lineTo(x - amplitude * dy * r, y + amplitude * dx * r);
////                            next += wavelength;
////                            phase++;
////                        }
////                    }
//                    next -= distance;
//                    first = false;
                    lastX = thisX;
                    lastY = thisY;
                    if (type == PathIterator.SEG_CLOSE)
                        result.closePath();
                    break;
            }
            it.next();
        }

        return base.createStrokedShape(result);
    }
}

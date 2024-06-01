package net.thevpc.halfa.engine.renderer.screen.common.strokes;

import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.tson.TsonElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompositeStroke implements Stroke {
    private Stroke[] strokes;

    public static Stroke of(TsonElement e) {
        ObjEx o = ObjEx.of(e);
        List<TsonElement> args = new ArrayList<>();
        args.addAll(o.args());
        args.addAll(o.body());
        return new CompositeStroke(
                args.stream().map(StrokeFactory::createStroke).toArray(Stroke[]::new)
        );
    }

    public static CompositeStroke of(Stroke... strokes) {
        return new CompositeStroke(strokes);
    }

    public CompositeStroke(Stroke... strokes) {
        this.strokes = strokes;
    }

    public Shape createStrokedShape(Shape shape) {
        Shape s = shape;
        for (int i = 0; i < strokes.length; i++) {
            s = strokes[i].createStrokedShape(s);
        }
        return s;
    }
}

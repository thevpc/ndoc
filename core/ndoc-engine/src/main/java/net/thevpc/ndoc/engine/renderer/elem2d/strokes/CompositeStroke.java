package net.thevpc.ndoc.engine.renderer.elem2d.strokes;

import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.nuts.elem.NElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompositeStroke implements Stroke {
    private Stroke[] strokes;

    public static Stroke of(NElement e, NDocGraphics g) {
        NDocObjEx o = NDocObjEx.of(e);
        List<NElement> args = new ArrayList<>();
        args.addAll(o.args());
        args.addAll(o.body());
        return new CompositeStroke(
                args.stream().map(g::createStroke).toArray(Stroke[]::new)
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

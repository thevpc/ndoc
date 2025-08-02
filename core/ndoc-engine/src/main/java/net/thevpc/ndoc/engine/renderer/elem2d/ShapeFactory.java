package net.thevpc.ndoc.engine.renderer.elem2d;

import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ShapeFactory {
    public static Shape createShape(NElement e) {
        NDocValue o = NDocValue.of(e);
        if (o.name() != null) {
            switch (o.name()) {
                case "star": {
                    double radius = 5;
                    for (NElement arg : o.args()) {
                        NOptional<NDocValue.SimplePair> sp = NDocValue.of(arg).asSimplePair();
                        if (sp.isPresent()) {
                            NDocValue.SimplePair ke = sp.get();
                            switch (NDocUtils.uid(ke.getName())) {
                                case "radius": {
                                    radius = ke.getValue().asDouble().orElse(radius);
                                    break;
                                }
                            }
                        }
                    }
                    return ShapeHelper.createDefaultStar(radius, 0, 0);
                }
            }
        }
        return new Ellipse2D.Float(0, 0, 4, 4);
    }

}

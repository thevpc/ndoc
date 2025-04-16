package net.thevpc.ndoc.engine.renderer.elem2d;

import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ShapeFactory {
    public static Shape createShape(NElement e) {
        NDocObjEx o = NDocObjEx.of(e);
        if (o.name() != null) {
            switch (o.name()) {
                case "star": {
                    double radius = 5;
                    for (NElement arg : o.args()) {
                        NOptional<NDocObjEx.SimplePair> sp = NDocObjEx.of(arg).asSimplePair();
                        if (sp.isPresent()) {
                            NDocObjEx.SimplePair ke = sp.get();
                            switch (HUtils.uid(ke.getName())) {
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

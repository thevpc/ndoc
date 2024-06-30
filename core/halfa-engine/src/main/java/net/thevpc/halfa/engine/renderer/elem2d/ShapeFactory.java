package net.thevpc.halfa.engine.renderer.common.elem2d;

import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ShapeFactory {
    public static Shape createShape(TsonElement e) {
        ObjEx o = ObjEx.of(e);
        if (o.name() != null) {
            switch (o.name()) {
                case "star": {
                    double radius = 5;
                    for (TsonElement arg : o.args()) {
                        NOptional<ObjEx.SimplePair> sp = ObjEx.of(arg).asSimplePair();
                        if (sp.isPresent()) {
                            ObjEx.SimplePair ke = sp.get();
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

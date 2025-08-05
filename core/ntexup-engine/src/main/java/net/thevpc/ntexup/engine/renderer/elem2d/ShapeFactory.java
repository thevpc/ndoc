package net.thevpc.ntexup.engine.renderer.elem2d;

import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.elem.NElement;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ShapeFactory {
    public static Shape createShape(NElement e) {
        NTxValue o = NTxValue.of(e);
        if (o.name() != null) {
            switch (o.name()) {
                case "star": {
                    double radius = 5;
                    for (NElement arg : o.args()) {
                        NOptional<NTxValue.SimplePair> sp = NTxValue.of(arg).asSimplePair();
                        if (sp.isPresent()) {
                            NTxValue.SimplePair ke = sp.get();
                            switch (NTxUtils.uid(ke.getName())) {
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

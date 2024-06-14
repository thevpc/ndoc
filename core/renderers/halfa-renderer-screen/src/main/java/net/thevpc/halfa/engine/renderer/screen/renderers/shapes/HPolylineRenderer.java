package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HElement2DFactory;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.ObjEx;

import java.awt.*;

public class HPolylineRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HPolylineRenderer() {
        super(HNodeType.POLYLINE);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        HGraphics g = ctx.graphics();
        HPoint2D[] points =  ObjEx.ofProp(p, HPropName.POINTS).asHPoint2DArray().get();
        if (!ctx.isDry()) {
            Paint fc = resolveLineColor(p, g, ctx, true);
            g.draw2D(HElement2DFactory.polyline(points)
                    .setLineStroke(resolveStroke(p, g, ctx))
                    .setLinePaint(fc));
        }
        paintDebugBox(p, ctx, g, b);
    }

}

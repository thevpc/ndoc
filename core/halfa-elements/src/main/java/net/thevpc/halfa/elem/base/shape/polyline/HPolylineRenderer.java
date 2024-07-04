package net.thevpc.halfa.elem.base.shape.polyline;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HElement2DFactory;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.eval.ObjEx;

import java.awt.*;

public class HPolylineRenderer extends HNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public HPolylineRenderer() {
        super(HNodeType.POLYLINE);
    }

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        HGraphics g = ctx.graphics();
        HPoint2D[] points = ObjEx.ofProp(p, HPropName.POINTS).asHPoint2DArray().get();
        if (!ctx.isDry()) {
            Paint fc = HValueByName.getForegroundColor(p, ctx, true);
            g.draw2D(HElement2DFactory.polyline(points)
                    .setLineStroke(HNodeRendererUtils.resolveStroke(p, g, ctx))
                    .setLinePaint(fc));
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}

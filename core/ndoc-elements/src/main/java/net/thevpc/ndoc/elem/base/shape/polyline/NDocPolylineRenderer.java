package net.thevpc.ndoc.elem.base.shape.polyline;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.HElement2DFactory;
import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.eval.NDocObjEx;

import java.awt.*;

public class NDocPolylineRenderer extends NDocNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public NDocPolylineRenderer() {
        super(HNodeType.POLYLINE);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        NDocGraphics g = ctx.graphics();
        HPoint2D[] points = NDocObjEx.ofProp(p, HPropName.POINTS).asHPoint2DArray().get();
        if (!ctx.isDry()) {
            Paint fc = NDocValueByName.getForegroundColor(p, ctx, true);
            g.draw2D(HElement2DFactory.polyline(points)
                    .setLineStroke(HNodeRendererUtils.resolveStroke(p, g, ctx))
                    .setLinePaint(fc));
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}

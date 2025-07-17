package net.thevpc.ndoc.elem.base.shape.polyline;

import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.elem2d.NDocElement2DFactory;
import net.thevpc.ndoc.api.model.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.spi.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.eval.NDocObjEx;

import java.awt.*;

public class NDocPolylineRenderer extends NDocNodeRendererBase {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocPolylineRenderer() {
        super(NDocNodeType.POLYLINE);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = selfBounds(p, ctx);
        NDocGraphics g = ctx.graphics();
        NDocPoint2D[] points = NDocObjEx.ofProp(p, NDocPropName.POINTS).asHPoint2DArray().get();
        if (!ctx.isDry()) {
            Paint fc = NDocValueByName.getForegroundColor(p, ctx, true);
            g.draw2D(NDocElement2DFactory.polyline(points)
                    .setLineStroke(NDocNodeRendererUtils.resolveStroke(p, g, ctx))
                    .setLinePaint(fc));
        }
        NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}

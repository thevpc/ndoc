package net.thevpc.ndoc.elem.base.shape.polyline;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocElement2DFactory;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.eval.NDocObjEx;

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

package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;

public class HPolylineRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HPolylineRenderer() {
        super(HNodeType.POLYLINE);
    }

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Bounds2 b = selfBounds(p, ctx);
        double x = b.getX();
        double y = b.getY();
        HGraphics g = ctx.graphics();
        Double2[] points =  ObjEx.ofProp(p, HPropName.POINTS).asDouble2Array().get();
        double[] xx = new double[points.length];
        double[] yy = new double[points.length];
        for (int i = 0; i < points.length; i++) {
            xx[i] = (HUtils.doubleOf(points[i].getX()) / 100 * b.getWidth() + x);
            yy[i] = (HUtils.doubleOf(points[i].getY()) / 100 * b.getHeight() + y);
        }
        if (!ctx.isDry()) {
            if (applyLineColor(p, g, ctx, true)) {
                g.drawPolyline(xx, yy, points.length);
            }
        }
        return new HSizeRequirements(b);
    }

}

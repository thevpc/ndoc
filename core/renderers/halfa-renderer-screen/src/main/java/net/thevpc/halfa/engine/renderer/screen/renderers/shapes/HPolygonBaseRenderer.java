package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;

public abstract class HPolygonBaseRenderer extends AbstractHNodeRenderer {
    public HPolygonBaseRenderer(String... types) {
        super(types);
    }

    public void render(HNode p, Double2[] points, HNodeRendererContext ctx) {
        Bounds2 b = selfBounds(p, ctx);
        double x = HUtils.doubleOf(b.getX());
        double y = HUtils.doubleOf(b.getY());
        HGraphics g = ctx.graphics();
        double[] xx = new double[points.length];
        double[] yy = new double[points.length];
        for (int i = 0; i < points.length; i++) {
            xx[i] = (HUtils.doubleOf(points[i].getX()) / 100 * b.getWidth() + x);
            yy[i] = (HUtils.doubleOf(points[i].getY()) / 100 * b.getHeight() + y);
        }
        boolean someBG;
        if(!ctx.isDry()) {
            if (someBG = applyBackgroundColor(p, g, ctx)) {
                g.fillPolygon(xx, yy, points.length);
            }
            if (applyLineColor(p, g, ctx, !someBG)) {
                applyStroke(p, g, ctx);
                g.drawPolygon(xx, yy, points.length);
            }
        }
        paintDebugBox(p, ctx, g, b);
    }

}

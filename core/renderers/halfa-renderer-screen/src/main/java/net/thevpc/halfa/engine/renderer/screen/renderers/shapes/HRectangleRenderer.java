package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HRectangle;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.PageView;
import net.thevpc.halfa.spi.HUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HRectangleRenderer extends AbstractHPartRenderer {


    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        HRectangle t = (HRectangle) p;

        Bounds2 b = selfBounds(t, ctx);
        double x = b.getX();
        double y = b.getY();
        Graphics2D g = ctx.getGraphics();
        Boolean threeD = get3D(p, ctx);
        Boolean raised = getRaised(p, ctx);
        if (raised != null) {
            if (threeD == null) {
                threeD = true;
            }
        }
        Double2 roundCorners = roundCornerArcs(p, ctx);

        boolean round = roundCorners != null;
        boolean d3 = threeD == null ? false : threeD;
        if (!round && !d3) {
            boolean someBG = false;
            if (someBG = applyBackgroundColor(t, g, ctx)) {
                g.fillRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
            }
            if (applyLineColor(t, g, ctx, !someBG)) {
                g.drawRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
            }
        } else if (round) {
            double cx = HUtils.doubleOf(roundCorners.getX()) / 100 * PageView.REF_SIZE.width;
            double cy = HUtils.doubleOf(roundCorners.getY()) / 100 * PageView.REF_SIZE.height;
            boolean someBG = false;
            if (someBG=applyBackgroundColor(t, g, ctx)) {
                g.fillRoundRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), (int) cx, (int) cy);
            }
            if (applyLineColor(t, g, ctx, !someBG)) {
                g.drawRoundRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), (int) cx, (int) cy);
            }
        } else if (threeD) {
            boolean someBG = false;
            if (someBG=applyBackgroundColor(t, g, ctx)) {
                g.fill3DRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), raised != null && raised);
            }
            if (applyLineColor(t, g, ctx, !someBG)) {
                g.draw3DRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), raised != null && raised);
            }
        }
        return new Bounds2(x, y, b.getWidth(), b.getWidth());
    }

}

package net.thevpc.halfa.elem.base.shape.polygon;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HUtils;

public class HRectangleRenderer extends HNodeRendererBase {
    HProperties rectangleStyles = new HProperties();
    HProperties squareDefaultStyles = new HProperties();

    public HRectangleRenderer() {
        super(HNodeType.RECTANGLE, HNodeType.SQUARE);
    }

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        HProperties defaultStyles = rectangleStyles;
        switch (p.type()) {
            case HNodeType.RECTANGLE: {
                defaultStyles = rectangleStyles;
                break;
            }
            case HNodeType.SQUARE: {
                defaultStyles = squareDefaultStyles;
                break;
            }
        }
        ctx = ctx.withDefaultStyles(p, defaultStyles);

        Bounds2 b = selfBounds(p, ctx);
        double x = b.getX();
        double y = b.getY();
        HGraphics g = ctx.graphics();
        Boolean threeD = HValueByName.get3D(p, ctx);
        Boolean raised = HValueByName.getRaised(p, ctx);
        if (raised != null) {
            if (threeD == null) {
                threeD = true;
            }
        }
        Double2 roundCorners = HValueByName.getRoundCornerArcs(p, ctx);

        boolean round = roundCorners != null;
        boolean d3 = threeD == null ? false : threeD;
        if (!ctx.isDry()) {
            if (!round && !d3) {
                boolean someBG = false;
                if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
                }
                if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                    HNodeRendererUtils.applyStroke(p, g, ctx);
                    g.drawRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
                }
            } else if (round) {
                double cx = HUtils.doubleOf(roundCorners.getX()) / 100 * ctx.getGlobalBounds().getWidth();
                double cy = HUtils.doubleOf(roundCorners.getY()) / 100 * ctx.getGlobalBounds().getHeight();
                boolean someBG = false;
                if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRoundRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                }
                if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                    HNodeRendererUtils.applyStroke(p, g, ctx);
                    g.drawRoundRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                }
            } else if (threeD) {
                boolean someBG = false;
                if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fill3DRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), raised != null && raised);
                }
                if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                    HNodeRendererUtils.applyStroke(p, g, ctx);
                    g.draw3DRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), raised != null && raised);
                }
            }
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}

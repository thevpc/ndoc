package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.Double2;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

public class NDocRectangleRenderer extends NDocNodeRendererBase {
    HProperties rectangleStyles = new HProperties();
    HProperties squareDefaultStyles = new HProperties();

    public NDocRectangleRenderer() {
        super(HNodeType.RECTANGLE, HNodeType.SQUARE);
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
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
        NDocGraphics g = ctx.graphics();
        Boolean threeD = NDocValueByName.get3D(p, ctx);
        Boolean raised = NDocValueByName.getRaised(p, ctx);
        if (raised != null) {
            if (threeD == null) {
                threeD = true;
            }
        }
        Double2 roundCorners = NDocValueByName.getRoundCornerArcs(p, ctx);

        boolean round = roundCorners != null;
        boolean d3 = threeD == null ? false : threeD;
        if (!ctx.isDry()) {
            if (!round && !d3) {
                boolean someBG = false;
                if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRect((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), net.thevpc.ndoc.api.util.HUtils.intOf(b.getHeight()));
                }
                if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                    HNodeRendererUtils.withStroke(p, g, ctx, () -> {
                        g.drawRect((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), net.thevpc.ndoc.api.util.HUtils.intOf(b.getHeight()));
                    });
                }
            } else if (round) {
                double cx = net.thevpc.ndoc.api.util.HUtils.doubleOf(roundCorners.getX()) / 100 * ctx.getGlobalBounds().getWidth();
                double cy = net.thevpc.ndoc.api.util.HUtils.doubleOf(roundCorners.getY()) / 100 * ctx.getGlobalBounds().getHeight();
                boolean someBG = false;
                if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRoundRect((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), net.thevpc.ndoc.api.util.HUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                }
                if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                    HNodeRendererUtils.withStroke(p, g, ctx, () -> {
                        g.drawRoundRect((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), net.thevpc.ndoc.api.util.HUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                    });
                }
            } else if (threeD) {
                boolean someBG = false;
                if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fill3DRect((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), net.thevpc.ndoc.api.util.HUtils.intOf(b.getHeight()), raised != null && raised);
                }
                if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                    HNodeRendererUtils.withStroke(p, g, ctx, () -> {
                        g.draw3DRect((int) x, (int) y, net.thevpc.ndoc.api.util.HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), raised != null && raised);
                    });
                }
            }
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}

package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class NDocRectangleRenderer extends NDocNodeRendererBase {
    NDocProperties rectangleStyles = new NDocProperties();
    NDocProperties squareDefaultStyles = new NDocProperties();

    public NDocRectangleRenderer() {
        super(NDocNodeType.RECTANGLE, NDocNodeType.SQUARE);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        NDocProperties defaultStyles = rectangleStyles;
        switch (p.type()) {
            case NDocNodeType.RECTANGLE: {
                defaultStyles = rectangleStyles;
                break;
            }
            case NDocNodeType.SQUARE: {
                defaultStyles = squareDefaultStyles;
                break;
            }
        }
        ctx = ctx.withDefaultStyles(p, defaultStyles);

        NDocBounds2 b = selfBounds(p, ctx);
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
        NDocDouble2 roundCorners = NDocValueByName.getRoundCornerArcs(p, ctx);

        boolean round = roundCorners != null;
        boolean d3 = threeD == null ? false : threeD;
        if (!ctx.isDry()) {
            if (!round && !d3) {
                boolean someBG = false;
                if (someBG = NDocNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
                }
                if (NDocNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                    NDocNodeRendererUtils.withStroke(p, g, ctx, () -> {
                        g.drawRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
                    });
                }
            } else if (round) {
                double cx = NDocUtils.doubleOf(roundCorners.getX()) / 100 * ctx.getGlobalBounds().getWidth();
                double cy = NDocUtils.doubleOf(roundCorners.getY()) / 100 * ctx.getGlobalBounds().getHeight();
                boolean someBG = false;
                if (someBG = NDocNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRoundRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                }
                if (NDocNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                    NDocNodeRendererUtils.withStroke(p, g, ctx, () -> {
                        g.drawRoundRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                    });
                }
            } else if (threeD) {
                boolean someBG = false;
                if (someBG = NDocNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fill3DRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()), raised != null && raised);
                }
                if (NDocNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
                    NDocNodeRendererUtils.withStroke(p, g, ctx, () -> {
                        g.draw3DRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()), raised != null && raised);
                    });
                }
            }
        }
        NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}

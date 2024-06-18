package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.engine.renderer.screen.common.HNodeRendererUtils;
import net.thevpc.halfa.spi.nodes.HPropValueByNameParser;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.PageView;
import net.thevpc.halfa.spi.util.HUtils;

public class HRectangleRenderer extends AbstractHNodeRenderer {
    HProperties rectangleStyles = new HProperties();
    HProperties squareDefaultStyles = new HProperties();

    public HRectangleRenderer() {
        super(HNodeType.RECTANGLE, HNodeType.SQUARE);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        HProperties defaultStyles=rectangleStyles;
        switch (p.type()){
            case HNodeType.RECTANGLE:{
                defaultStyles=rectangleStyles;
                break;
            }
            case HNodeType.SQUARE:{
                defaultStyles=squareDefaultStyles;
                break;
            }
        }
        ctx=ctx.withDefaultStyles(p,defaultStyles);

        Bounds2 b = selfBounds(p, ctx);
        double x = b.getX();
        double y = b.getY();
        HGraphics g = ctx.graphics();
        Boolean threeD = HPropValueByNameParser.get3D(p, ctx);
        Boolean raised = HPropValueByNameParser.getRaised(p, ctx);
        if (raised != null) {
            if (threeD == null) {
                threeD = true;
            }
        }
        Double2 roundCorners = HPropValueByNameParser.getRoundCornerArcs(p, ctx);

        boolean round = roundCorners != null;
        boolean d3 = threeD == null ? false : threeD;
        if (!ctx.isDry()) {
            if (!round && !d3) {
                boolean someBG = false;
                if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
                }
                if (HNodeRendererUtils.applyLineColor(p, g, ctx, !someBG)) {
                    HNodeRendererUtils.applyStroke(p, g, ctx);
                    g.drawRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()));
                }
            } else if (round) {
                double cx = HUtils.doubleOf(roundCorners.getX()) / 100 * PageView.REF_SIZE.width;
                double cy = HUtils.doubleOf(roundCorners.getY()) / 100 * PageView.REF_SIZE.height;
                boolean someBG = false;
                if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRoundRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                }
                if (HNodeRendererUtils.applyLineColor(p, g, ctx, !someBG)) {
                    HNodeRendererUtils.applyStroke(p, g, ctx);
                    g.drawRoundRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                }
            } else if (threeD) {
                boolean someBG = false;
                if (someBG = HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fill3DRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), raised != null && raised);
                }
                if (HNodeRendererUtils.applyLineColor(p, g, ctx, !someBG)) {
                    HNodeRendererUtils.applyStroke(p, g, ctx);
                    g.draw3DRect((int) x, (int) y, HUtils.intOf(b.getWidth()), HUtils.intOf(b.getHeight()), raised != null && raised);
                }
            }
        }
        HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
    }

}

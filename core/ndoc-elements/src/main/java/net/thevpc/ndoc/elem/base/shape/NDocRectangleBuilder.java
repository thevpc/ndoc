package net.thevpc.ndoc.elem.base.shape;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.util.NDocUtils;

public class NDocRectangleBuilder implements NDocNodeCustomBuilder {
    private NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.RECTANGLE)
                .parseParam().named(NDocPropName.ROUND_CORNER, NDocPropName.THEED, NDocPropName.RAISED).asFlags().then()
                .renderComponent(this::renderMain)
        ;
    }


    public void renderMain(NDocNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);

        NDocBounds2 b = rendererContext.selfBounds(p);
        double x = b.getX();
        double y = b.getY();
        NDocGraphics g = rendererContext.graphics();
        Boolean threeD = NDocValueByName.get3D(p, rendererContext);
        Boolean raised = NDocValueByName.getRaised(p, rendererContext);
        if (raised != null) {
            if (threeD == null) {
                threeD = true;
            }
        }
        NDocDouble2 roundCorners = NDocValueByName.getRoundCornerArcs(p, rendererContext);
        boolean round = roundCorners != null;
        boolean d3 = threeD == null ? false : threeD;
        if (!rendererContext.isDry()) {
            if (!round && !d3) {
                boolean someBG = false;
                if (someBG = NDocNodeRendererUtils.applyBackgroundColor(p, g, rendererContext)) {
                    g.fillRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
                }
                if (NDocNodeRendererUtils.applyForeground(p, g, rendererContext, !someBG)) {
                    NDocNodeRendererUtils.withStroke(p, g, rendererContext, () -> {
                        g.drawRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
                    });
                }
            } else if (round) {
                double cx = NDocUtils.doubleOf(roundCorners.getX()) / 100 * rendererContext.getGlobalBounds().getWidth();
                double cy = NDocUtils.doubleOf(roundCorners.getY()) / 100 * rendererContext.getGlobalBounds().getHeight();
                boolean someBG = false;
                if (someBG = NDocNodeRendererUtils.applyBackgroundColor(p, g, rendererContext)) {
                    g.fillRoundRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                }
                if (NDocNodeRendererUtils.applyForeground(p, g, rendererContext, !someBG)) {
                    NDocNodeRendererUtils.withStroke(p, g, rendererContext, () -> {
                        g.drawRoundRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()), (int) cx, (int) cy);
                    });
                }
            } else if (threeD) {
                boolean someBG = false;
                if (someBG = NDocNodeRendererUtils.applyBackgroundColor(p, g, rendererContext)) {
                    g.fill3DRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()), raised != null && raised);
                }
                if (NDocNodeRendererUtils.applyForeground(p, g, rendererContext, !someBG)) {
                    NDocNodeRendererUtils.withStroke(p, g, rendererContext, () -> {
                        g.draw3DRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()), raised != null && raised);
                    });
                }
            }
        }
        NDocNodeRendererUtils.paintDebugBox(p, rendererContext, g, b);
    }
}

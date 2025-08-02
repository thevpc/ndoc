package net.thevpc.ndoc.engine.base.nodes.shape;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocUtils;

public class NDocSquareBuilder implements NDocNodeBuilder {
    private NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.SQUARE)
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
        Boolean threeD = rendererContext.get3D(p);
        Boolean raised = rendererContext.getRaised(p);
        if (raised != null) {
            if (threeD == null) {
                threeD = true;
            }
        }
        NDocDouble2 roundCorners = rendererContext.getRoundCornerArcs(p);
        boolean round = roundCorners != null;
        boolean d3 = threeD == null ? false : threeD;
        if (!rendererContext.isDry()) {
            int ww = NDocUtils.intOf(b.getWidth());
            int hh = NDocUtils.intOf(b.getHeight());
            ww = Math.min(ww, hh);
            hh = ww;
            int finalWw = ww;
            int finalHh = hh;
            if (!round && !d3) {
                boolean someBG = false;
                if (someBG = rendererContext.applyBackgroundColor(p)) {
                    g.fillRect((int) x, (int) y, ww, hh);
                }
                if (rendererContext.applyForeground(p, !someBG)) {
                    rendererContext.withStroke(p, () -> {
                        g.drawRect((int) x, (int) y, finalWw, finalHh);
                    });
                }
            } else if (round) {
                double cx = NDocUtils.doubleOf(roundCorners.getX()) / 100 * rendererContext.getGlobalBounds().getWidth();
                double cy = NDocUtils.doubleOf(roundCorners.getY()) / 100 * rendererContext.getGlobalBounds().getHeight();
                boolean someBG = false;
                if (someBG = rendererContext.applyBackgroundColor(p)) {
                    g.fillRoundRect((int) x, (int) y, ww, hh, (int) cx, (int) cy);
                }
                if (rendererContext.applyForeground(p, !someBG)) {
                    rendererContext.withStroke(p, () -> {
                        g.drawRoundRect((int) x, (int) y, finalWw, finalHh, (int) cx, (int) cy);
                    });
                }
            } else if (threeD) {
                boolean someBG = false;
                if (someBG = rendererContext.applyBackgroundColor(p)) {
                    g.fill3DRect((int) x, (int) y, ww, hh, raised != null && raised);
                }
                if (rendererContext.applyForeground(p, !someBG)) {
                    rendererContext.withStroke(p, () -> {
                        g.draw3DRect((int) x, (int) y, finalWw, finalHh, raised != null && raised);
                    });
                }
            }
        }
        rendererContext.paintDebugBox(p, b);
    }

}

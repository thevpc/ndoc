package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxUtils;

public class NTxSquareBuilder implements NTxNodeBuilder {
    private NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NTxNodeType.SQUARE)
                .parseParam().named(NTxPropName.ROUND_CORNER, NTxPropName.THEED, NTxPropName.RAISED).asFlags().then()
                .renderComponent(this::renderMain)
        ;
    }


    public void renderMain(NTxNode p, NTxNodeRendererContext rendererContext, NTxNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);

        NTxBounds2 b = rendererContext.selfBounds(p);
        double x = b.getX();
        double y = b.getY();
        NTxGraphics g = rendererContext.graphics();
        Boolean threeD = rendererContext.get3D(p);
        Boolean raised = rendererContext.getRaised(p);
        if (raised != null) {
            if (threeD == null) {
                threeD = true;
            }
        }
        NTxDouble2 roundCorners = rendererContext.getRoundCornerArcs(p);
        boolean round = roundCorners != null;
        boolean d3 = threeD == null ? false : threeD;
        if (!rendererContext.isDry()) {
            int ww = NTxUtils.intOf(b.getWidth());
            int hh = NTxUtils.intOf(b.getHeight());
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
                double cx = NTxUtils.doubleOf(roundCorners.getX()) / 100 * rendererContext.getGlobalBounds().getWidth();
                double cy = NTxUtils.doubleOf(roundCorners.getY()) / 100 * rendererContext.getGlobalBounds().getHeight();
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

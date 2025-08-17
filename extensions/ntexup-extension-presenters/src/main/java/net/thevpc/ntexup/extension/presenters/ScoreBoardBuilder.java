package net.thevpc.ntexup.extension.presenters;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.engine.NTxNodeBuilderContext;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

import java.awt.*;


/**
 *
 */
public class ScoreBoardBuilder implements NTxNodeBuilder {

    @Override
    public void build(NTxNodeBuilderContext builderContext) {
        builderContext.id("scoreboard")
                .renderComponent((ctx, builderContext1) -> renderMain(ctx, builderContext1))
        ;
    }


    public void renderMain(NTxNodeRendererContext rendererContext, NTxNodeBuilderContext builderContext) {
        NTxNode node = rendererContext.node();
        NTxBounds2 b = rendererContext.selfBounds(node, null, null);

        Paint color = rendererContext.getForegroundColor(node, true);

        NTxGraphics g = rendererContext.graphics();
        if (!rendererContext.isDry()) {
            g.setPaint(color);
        }
        rendererContext.drawContour();
    }

}

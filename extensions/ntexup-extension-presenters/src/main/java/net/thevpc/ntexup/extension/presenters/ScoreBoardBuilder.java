package net.thevpc.ntexup.extension.presenters;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

import java.awt.*;


/**
 *
 */
public class ScoreBoardBuilder implements NTxNodeBuilder {

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id("scoreboard")
                .renderComponent(this::renderMain)
        ;
    }


    public void renderMain(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        NTxBounds2 b = ctx.selfBounds(p, null, null);

        Paint color = ctx.getForegroundColor(p, true);

        NTxGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            g.setPaint(color);


            ctx.paintDebugBox(p , b);
        }
    }

}

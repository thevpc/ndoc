package net.thevpc.ntexup.extension.presenters;

import net.thevpc.ntexup.api.document.elem2d.NDocBounds2;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

import java.awt.*;


/**
 *
 */
public class ScoreBoardBuilder implements NDocNodeBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id("scoreboard")
                .renderComponent(this::renderMain)
        ;
    }


    public void renderMain(NTxNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext builderContext) {
        NDocBounds2 b = ctx.selfBounds(p, null, null);

        Paint color = ctx.getForegroundColor(p, true);

        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            g.setPaint(color);


            ctx.paintDebugBox(p , b);
        }
    }

}

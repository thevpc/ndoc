package net.thevpc.ndoc.extension.presenters;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;

import java.awt.*;


/**
 *
 */
public class ScoreBoardBuilder implements NDocNodeCustomBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id("scoreboard")
                .renderComponent(this::renderMain)
        ;
    }


    public void renderMain(NDocNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext builderContext) {
        NDocBounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);

        Paint color = NDocValueByName.getForegroundColor(p, ctx, true);

        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            g.setPaint(color);


            NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
        }
    }

}

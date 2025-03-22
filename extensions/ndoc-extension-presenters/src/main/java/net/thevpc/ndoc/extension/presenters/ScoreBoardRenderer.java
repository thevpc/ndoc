package net.thevpc.ndoc.extension.presenters;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.awt.*;

public class ScoreBoardRenderer extends NDocNodeRendererBase {
    public ScoreBoardRenderer() {
        super("scoreboard");
    }

    @Override
    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        Bounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);

        Paint color = NDocValueByName.getForegroundColor(p, ctx, true);

        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            g.setPaint(color);


            HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
        }
    }


}

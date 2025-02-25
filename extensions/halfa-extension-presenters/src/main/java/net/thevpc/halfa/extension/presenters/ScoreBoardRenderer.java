package net.thevpc.halfa.extension.presenters;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.util.MinMax;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public class ScoreBoardRenderer extends HNodeRendererBase {
    public ScoreBoardRenderer() {
        super("scoreboard");
    }

    @Override
    public void renderMain(HNode p, HNodeRendererContext ctx) {
        Bounds2 b = HValueByName.selfBounds(p, null, null, ctx);

        Paint color = HValueByName.getForegroundColor(p, ctx, true);

        HGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            g.setPaint(color);


            HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
        }
    }


}

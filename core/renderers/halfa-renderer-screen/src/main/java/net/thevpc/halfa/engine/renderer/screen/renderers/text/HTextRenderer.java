package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HText;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HTextRenderer extends AbstractHPartRenderer {

    @Override
    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        HText t = (HText) p;
        String message = t.value();
        if (message == null) {
            message = "";
        }
        Graphics2D g = ctx.getGraphics();
        applyFont(t, g, ctx);
        Rectangle2D b = g.getFontMetrics().getStringBounds(message, g);
        double ww = b.getWidth();
        double hh = b.getHeight();
        HSizeRequirements r = new HSizeRequirements();
        r.minX = ww;
        r.maxX = ww;
        r.preferredX = ww;
        r.minY = hh;
        r.maxY = hh;
        r.preferredY = hh;
        return r;
    }

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        HText t = (HText) p;
        String message = t.value();
        if (message == null) {
            message = "";
        }
        Graphics2D g = ctx.getGraphics();
        applyFont(t, g, ctx);
        String[] allLines = message.trim().split("[\n]");
        double[] lineYOffset = new double[allLines.length];
        Rectangle2D[] textBounds = new Rectangle2D[allLines.length];
        double fullHeight = 0;
        double fullWidth = 0;
        for (int i = 0; i < textBounds.length; i++) {
            allLines[i]=allLines[i].trim();
            textBounds[i] = g.getFontMetrics().getStringBounds(allLines[i], g);
            fullHeight += textBounds[i].getHeight();
            fullWidth = Math.max(textBounds[i].getWidth(), fullWidth);
            if (i == 0) {
                lineYOffset[i] = -textBounds[i].getMinY();
            } else {
                lineYOffset[i] = lineYOffset[i - 1] + textBounds[i - 1].getHeight() ;//+ textBounds[i].getMinY();
            }
        }

        Bounds2 selfBounds = selfBounds(t, new Double2(fullWidth, fullHeight), ctx);
        double x = selfBounds.getX();
        double y = selfBounds.getY();

//        Bounds2 b2 = new Bounds2(offset.getX() + x, offset.getY() + y, b.getWidth(), b.getHeight());
//        Bounds2 b1 = new Bounds2(offset.getX() + b.getMinX(), offset.getY() + b.getMinY(), b.getWidth(), b.getHeight());
        paintBackground(t, ctx, g, selfBounds);

        applyForeground(t, g, ctx);
        for (int i = 0; i < textBounds.length; i++) {
            g.drawString(allLines[i], (int) x, (int) (y + lineYOffset[i]));
        }
        paintBorderLine(t, ctx, g, selfBounds);
        return selfBounds;
    }


}

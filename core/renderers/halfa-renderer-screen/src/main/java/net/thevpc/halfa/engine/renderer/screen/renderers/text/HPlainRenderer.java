package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.geom.Rectangle2D;

public class HPlainRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HPlainRenderer() {
        super(HNodeType.PLAIN);
    }

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        String message = NStringUtils.trim((String) (p.getPropertyValue(HPropName.VALUE).orElse("")));
        HGraphics g = ctx.graphics();
        applyFont(p, g, ctx);
        String[] allLines = message.trim().split("[\n]");
        double[] lineYOffset = new double[allLines.length];
        Rectangle2D[] textBounds = new Rectangle2D[allLines.length];
        double fullHeight = 0;
        double fullWidth = 0;
        for (int i = 0; i < textBounds.length; i++) {
            allLines[i]=allLines[i].trim();
            textBounds[i] = g.getStringBounds(allLines[i]);
            fullHeight += textBounds[i].getHeight();
            fullWidth = Math.max(textBounds[i].getWidth(), fullWidth);
            if (i == 0) {
                lineYOffset[i] = -textBounds[i].getMinY();
            } else {
                lineYOffset[i] = lineYOffset[i - 1] + textBounds[i - 1].getHeight() ;//+ textBounds[i].getMinY();
            }
        }

        Bounds2 bgBounds = selfBounds(p, null, ctx);
        Bounds2 selfBounds = selfBounds(p, new Double2(fullWidth, fullHeight), ctx);
        bgBounds=bgBounds.expand(selfBounds);
        double x = selfBounds.getX();
        double y = selfBounds.getY();

//        Bounds2 b2 = new Bounds2(offset.getX() + x, offset.getY() + y, b.getWidth(), b.getHeight());
//        Bounds2 b1 = new Bounds2(offset.getX() + b.getMinX(), offset.getY() + b.getMinY(), b.getWidth(), b.getHeight());
        if (!ctx.isDry()) {
            paintBackground(p, ctx, g, bgBounds);

            applyForeground(p, g, ctx);
            for (int i = 0; i < textBounds.length; i++) {
                g.drawString(allLines[i], x, (y + lineYOffset[i]));
            }
            paintBorderLine(p, ctx, g, selfBounds);
        }
        return new HSizeRequirements(selfBounds);
    }


}

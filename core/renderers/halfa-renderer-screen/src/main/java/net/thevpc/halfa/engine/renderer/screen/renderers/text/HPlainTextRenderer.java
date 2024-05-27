package net.thevpc.halfa.engine.renderer.screen.renderers.text;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class HPlainTextRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HPlainTextRenderer() {
        super(HNodeType.PLAIN);
    }

    @Override
    public HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx) {
        Bounds2 s = selfBounds(p, ctx);
        Bounds2 bb = ctx.getBounds();
        return new HSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public Bounds2 bgBounds(HNode p, HNodeRendererContext ctx) {
        return selfBounds(p, null, null, ctx);
    }

    public Bounds2 selfBounds(HNode p, HNodeRendererContext ctx) {
        String message = NStringUtils.trim((String) (p.getPropertyValue(HPropName.VALUE).orElse("")));
        HGraphics g = ctx.graphics();
        applyFont(p, g, ctx);
        String[] allLines = message.trim().split("[\n]");
        double[] lineYOffset = new double[allLines.length];
        Rectangle2D[] textBounds = new Rectangle2D[allLines.length];
        double fullHeight = 0;
        double fullWidth = 0;
        for (int i = 0; i < textBounds.length; i++) {
            allLines[i] = allLines[i].trim();
            textBounds[i] = g.getStringBounds(allLines[i]);
            fullHeight += textBounds[i].getHeight();
            fullWidth = Math.max(textBounds[i].getWidth(), fullWidth);
            if (i == 0) {
                lineYOffset[i] = -textBounds[i].getMinY();
            } else {
                lineYOffset[i] = lineYOffset[i - 1] + textBounds[i - 1].getHeight();//+ textBounds[i].getMinY();
            }
        }
        return selfBounds(p, new Double2(fullWidth, fullHeight), null, ctx);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        String message = NStringUtils.trim((String) (p.getPropertyValue(HPropName.VALUE).orElse("")));
        HGraphics g = ctx.graphics();
        applyFont(p, g, ctx);
        String[] allLines = message.trim().split("[\n]");
        double[] lineYOffset = new double[allLines.length];
        Rectangle2D[] textBounds = new Rectangle2D[allLines.length];
        for (int i = 0; i < textBounds.length; i++) {
            allLines[i] = allLines[i].trim();
            textBounds[i] = g.getStringBounds(allLines[i]);
            if (i == 0) {
                lineYOffset[i] = -textBounds[i].getMinY();
            } else {
                lineYOffset[i] = lineYOffset[i - 1] + textBounds[i - 1].getHeight();//+ textBounds[i].getMinY();
            }
        }

        Bounds2 bgBounds0 = bgBounds(p, ctx);
        Bounds2 bgBounds = bgBounds0;
        Bounds2 selfBounds = selfBounds(p, ctx);
        bgBounds = bgBounds.expand(selfBounds);

        HNodeRendererContext finalCtx = ctx;
        if(getDebugLevel(p, ctx)>=10) {
            g.debugString(
                    "Plain:\n"
                            + "expected=" + bgBounds0 + "\n"
                            + "fullSize=" + selfBounds + "\n"
                            + "newExpectedBounds=" + bgBounds + "\n"
                            + "curr: " +
                            Arrays.asList(
                                            HPropName.SIZE,
                                            HPropName.ORIGIN,
                                            HPropName.POSITION
                                    )
                                    .stream().map(x ->
                                            p.getProperty(x).orNull()
                                    ).filter(x -> x != null).collect(Collectors.toList())
                            + "\n"
                            + "eff: " +
                            Arrays.asList(
                                            HPropName.SIZE,
                                            HPropName.ORIGIN,
                                            HPropName.POSITION
                                    )
                                    .stream().map(x ->
                                            {
                                                Object n = finalCtx.getProperty(p, x).orNull();
                                                if (n == null) {
                                                    return n;
                                                }
                                                return new HProp(x, n);
                                            }
                                    ).filter(x -> x != null).collect(Collectors.toList())
                            + "\n"
                    ,
                    30, 100
            );
        }

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
    }


}

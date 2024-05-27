package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

import java.awt.*;
import java.util.List;

public class HFlowContainerRenderer extends AbstractHNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public HFlowContainerRenderer() {
        super(HNodeType.FLOW);
    }

    private static class Elems {
        Elem[] elems;
        Double2 size;
        Double2 fullSize;
    }

    private static class Elem {
        HNode node;
        HSizeRequirements sizeRequirements;
        Bounds2 bounds;
    }

    private Elems compute(HNode p, Bounds2 expectedBounds,HNodeRendererContext ctx) {
        List<HNode> texts = p.children();
        Elems e = new Elems();
        e.elems = new Elem[texts.size()];
        double allWidth = 0;
        double allHeight = 0;

        Double expectedWidth = expectedBounds.getWidth();
        Double expectedHeight = expectedBounds.getHeight();
        double xRef = expectedBounds.getX();
        double yRef = expectedBounds.getY();
        HNodeRendererContext ctx2 = ctx.withBounds(p, new Bounds2(0,0, expectedWidth, expectedHeight));
        for (int i = 0; i < texts.size(); i++) {
            HNode text = texts.get(i);
            HSizeRequirements ee = ctx2.sizeRequirementsOf(text);
            double w = ee.minX;
            if (w <= 0) {
                w = 10;
            }
            double h = ee.minY;
            if (h <= 0) {
                h = 10;
            }
            Elem zz = new Elem();
            e.elems[i] = zz;
            zz.node = text;
            zz.bounds = new Bounds2(xRef, yRef, w, h);
            if (e.size == null) {
                allWidth = zz.bounds.getWidth();
                allHeight = zz.bounds.getHeight();
                e.size = new Double2(allWidth, allHeight);
            } else {
                allWidth += zz.bounds.getWidth();
                allHeight = Math.max(zz.bounds.getHeight(), allHeight);
                e.size = new Double2(allWidth, allHeight);
            }
            xRef += w;
        }
        double w=Math.max(expectedWidth,e.size==null?0:e.size.getX());
        double h=Math.max(expectedHeight,e.size==null?0:e.size.getY());
        e.fullSize=new Double2(w,h);
        return e;
    }

    public HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx) {
        Bounds2 bg = selfBounds(p, ctx);
        Elems ee = compute(p, bg,ctx);
        return new HSizeRequirements(
                ee.size.getX(),
                ee.fullSize.getX(),
                ee.fullSize.getX(),
                ee.size.getY(),
                ee.fullSize.getY(),
                ee.fullSize.getY()
        );
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        HGraphics g = ctx.graphics();

        Bounds2 bg = selfBounds(p, ctx);
        Elems ee = compute(p, bg,ctx);
        Bounds2 newExpectedBounds =selfBounds(p, ee.size,null, ctx);

//        g.setColor(Color.BLUE);
//        g.drawRect(newExpectedBounds);
        if(getDebugLevel(p, ctx)>=10) {
            g.debugString(
                    "Flow:\n"
                            + "expected=" + bg + "\n"
                            + "fullSize=" + ee.fullSize.toString() + "\n"
                            + "newExpectedBounds=" + newExpectedBounds.toString(),
                    30, 30
            );
        }
        HNodeRendererContext ctx2 = ctx.withBounds(p, newExpectedBounds);
        ee = compute(p, newExpectedBounds,ctx2);

        bg=bg.expand(newExpectedBounds);
        if (!ctx.isDry()) {
            paintBackground(p, ctx, g, bg);
        }

        for (Elem elem : ee.elems) {
            HNodeRendererContext ctx3 = ctx.withBounds(p, elem.bounds);
            ctx3.render(elem.node);
        }


        paintBorderLine(p, ctx, g, bg);
    }
}

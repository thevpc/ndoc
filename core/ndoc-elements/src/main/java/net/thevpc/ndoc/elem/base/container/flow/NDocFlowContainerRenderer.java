package net.thevpc.ndoc.elem.base.container.flow;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.Double2;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.util.List;
import java.util.stream.Collectors;

public class NDocFlowContainerRenderer extends NDocNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public NDocFlowContainerRenderer() {
        super(HNodeType.FLOW);
    }

    private static class Elems {
        Elem[] elems;
        Double2 size;
        Double2 fullSize;
    }

    private static class Elem {
        HNode node;
        NDocSizeRequirements sizeRequirements;
        Bounds2 bounds;
    }

    private Elems compute(HNode p, Bounds2 expectedBounds, NDocNodeRendererContext ctx) {
        List<HNode> texts = p.children()
                .stream().filter(x -> NDocValueByName.isVisible(x, ctx)).collect(Collectors.toList());
        Elems e = new Elems();
        e.elems = new Elem[texts.size()];
        double allWidth = 0;
        double allHeight = 0;

        Double expectedWidth = expectedBounds.getWidth();
        Double expectedHeight = expectedBounds.getHeight();
        double xRef = expectedBounds.getX();
        double yRef = expectedBounds.getY();
        NDocNodeRendererContext ctx2 = ctx.withBounds(p, new Bounds2(0, 0, expectedWidth, expectedHeight));
        for (int i = 0; i < texts.size(); i++) {
            HNode text = texts.get(i);
            NDocSizeRequirements ee = ctx2.sizeRequirementsOf(text);
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
        double w = Math.max(expectedWidth, e.size == null ? 0 : e.size.getX());
        double h = Math.max(expectedHeight, e.size == null ? 0 : e.size.getY());
        e.fullSize = new Double2(w, h);
        return e;
    }

    public NDocSizeRequirements sizeRequirements(HNode p, NDocNodeRendererContext ctx) {
        Bounds2 bg = selfBounds(p, ctx);
        Elems ee = compute(p, bg, ctx);
        return new NDocSizeRequirements(
                ee.size.getX(),
                ee.fullSize.getX(),
                ee.fullSize.getX(),
                ee.size.getY(),
                ee.fullSize.getY(),
                ee.fullSize.getY()
        );
    }

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocGraphics g = ctx.graphics();

        Bounds2 bg = selfBounds(p, ctx);
        Elems ee = compute(p, bg, ctx);
        Bounds2 newExpectedBounds = NDocValueByName.selfBounds(p, ee.size, null, ctx);

//        g.setColor(Color.BLUE);
//        g.drawRect(newExpectedBounds);
        if (NDocValueByName.getDebugLevel(p, ctx) >= 10) {
            g.debugString(
                    "Flow:\n"
                            + "expected=" + bg + "\n"
                            + "fullSize=" + ee.fullSize.toString() + "\n"
                            + "newExpectedBounds=" + newExpectedBounds.toString(),
                    30, 30
            );
        }
        NDocNodeRendererContext ctx2 = ctx.withBounds(p, newExpectedBounds);
        ee = compute(p, newExpectedBounds, ctx2);

        bg = bg.expand(newExpectedBounds);
        if (!ctx.isDry()) {
            HNodeRendererUtils.paintBackground(p, ctx, g, bg);
        }

        for (Elem elem : ee.elems) {
            NDocNodeRendererContext ctx3 = ctx.withBounds(p, elem.bounds);
            ctx3.render(elem.node);
        }


        HNodeRendererUtils.paintBorderLine(p, ctx, g, bg);
    }
}

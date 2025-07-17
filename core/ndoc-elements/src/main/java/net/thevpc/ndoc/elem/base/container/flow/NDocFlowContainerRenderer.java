package net.thevpc.ndoc.elem.base.container.flow;

import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.spi.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.util.List;
import java.util.stream.Collectors;

public class NDocFlowContainerRenderer extends NDocNodeRendererBase {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocFlowContainerRenderer() {
        super(NDocNodeType.FLOW);
    }

    private static class Elems {
        Elem[] elems;
        NDocDouble2 size;
        NDocDouble2 fullSize;
    }

    private static class Elem {
        NDocNode node;
        NDocSizeRequirements sizeRequirements;
        NDocBounds2 bounds;
    }

    private Elems compute(NDocNode p, NDocBounds2 expectedBounds, NDocNodeRendererContext ctx) {
        List<NDocNode> texts = p.children()
                .stream().filter(x -> NDocValueByName.isVisible(x, ctx)).collect(Collectors.toList());
        Elems e = new Elems();
        e.elems = new Elem[texts.size()];
        double allWidth = 0;
        double allHeight = 0;

        Double expectedWidth = expectedBounds.getWidth();
        Double expectedHeight = expectedBounds.getHeight();
        double xRef = expectedBounds.getX();
        double yRef = expectedBounds.getY();
        NDocNodeRendererContext ctx2 = ctx.withBounds(p, new NDocBounds2(0, 0, expectedWidth, expectedHeight));
        for (int i = 0; i < texts.size(); i++) {
            NDocNode text = texts.get(i);
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
            zz.bounds = new NDocBounds2(xRef, yRef, w, h);
            if (e.size == null) {
                allWidth = zz.bounds.getWidth();
                allHeight = zz.bounds.getHeight();
                e.size = new NDocDouble2(allWidth, allHeight);
            } else {
                allWidth += zz.bounds.getWidth();
                allHeight = Math.max(zz.bounds.getHeight(), allHeight);
                e.size = new NDocDouble2(allWidth, allHeight);
            }
            xRef += w;
        }
        double w = Math.max(expectedWidth, e.size == null ? 0 : e.size.getX());
        double h = Math.max(expectedHeight, e.size == null ? 0 : e.size.getY());
        e.fullSize = new NDocDouble2(w, h);
        return e;
    }

    public NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext ctx) {
        NDocBounds2 bg = selfBounds(p, ctx);
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

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocGraphics g = ctx.graphics();

        NDocBounds2 bg = selfBounds(p, ctx);
        Elems ee = compute(p, bg, ctx);
        NDocBounds2 newExpectedBounds = NDocValueByName.selfBounds(p, ee.size, null, ctx);

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
            NDocNodeRendererUtils.paintBackground(p, ctx, g, bg);
        }

        for (Elem elem : ee.elems) {
            NDocNodeRendererContext ctx3 = ctx.withBounds(p, elem.bounds);
            ctx3.render(elem.node);
        }


        NDocNodeRendererUtils.paintBorderLine(p, ctx, g, bg);
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.container;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.model.NDocSizeRequirements;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class NDocFlowContainerBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();
    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.FLOW)
                .renderComponent(this::renderMain)
                .sizeRequirements(this::sizeRequirements);
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

    public NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        NDocBounds2 bg = ctx.selfBounds(p);
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

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocGraphics g = ctx.graphics();

        NDocBounds2 bg = ctx.selfBounds(p);
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

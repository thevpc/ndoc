/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.container;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.NTxSizeRequirements;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class NTxFlowContainerBuilder implements NTxNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();
    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.FLOW)
                .renderComponent(this::renderMain)
                .sizeRequirements(this::sizeRequirements);
    }



    private static class Elems {
        Elem[] elems;
        NTxDouble2 size;
        NTxDouble2 fullSize;
    }

    private static class Elem {
        NTxNode node;
        NTxSizeRequirements sizeRequirements;
        NTxBounds2 bounds;
    }

    private Elems compute(NTxNode p, NTxBounds2 expectedBounds, NTxNodeRendererContext ctx) {
        List<NTxNode> texts = p.children()
                .stream().filter(x -> ctx.isVisible(x)).collect(Collectors.toList());
        Elems e = new Elems();
        e.elems = new Elem[texts.size()];
        double allWidth = 0;
        double allHeight = 0;

        Double expectedWidth = expectedBounds.getWidth();
        Double expectedHeight = expectedBounds.getHeight();
        double xRef = expectedBounds.getX();
        double yRef = expectedBounds.getY();
        NTxNodeRendererContext ctx2 = ctx.withBounds(p, new NTxBounds2(0, 0, expectedWidth, expectedHeight));
        for (int i = 0; i < texts.size(); i++) {
            NTxNode text = texts.get(i);
            NTxSizeRequirements ee = ctx2.sizeRequirementsOf(text);
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
            zz.bounds = new NTxBounds2(xRef, yRef, w, h);
            if (e.size == null) {
                allWidth = zz.bounds.getWidth();
                allHeight = zz.bounds.getHeight();
                e.size = new NTxDouble2(allWidth, allHeight);
            } else {
                allWidth += zz.bounds.getWidth();
                allHeight = Math.max(zz.bounds.getHeight(), allHeight);
                e.size = new NTxDouble2(allWidth, allHeight);
            }
            xRef += w;
        }
        double w = Math.max(expectedWidth, e.size == null ? 0 : e.size.getX());
        double h = Math.max(expectedHeight, e.size == null ? 0 : e.size.getY());
        e.fullSize = new NTxDouble2(w, h);
        return e;
    }

    public NTxSizeRequirements sizeRequirements(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        NTxBounds2 bg = ctx.selfBounds(p);
        Elems ee = compute(p, bg, ctx);
        return new NTxSizeRequirements(
                ee.size.getX(),
                ee.fullSize.getX(),
                ee.fullSize.getX(),
                ee.size.getY(),
                ee.fullSize.getY(),
                ee.fullSize.getY()
        );
    }

    public void renderMain(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxGraphics g = ctx.graphics();

        NTxBounds2 bg = ctx.selfBounds(p);
        Elems ee = compute(p, bg, ctx);
        NTxBounds2 newExpectedBounds = ctx.selfBounds(p, ee.size, null);

//        g.setColor(Color.BLUE);
//        g.drawRect(newExpectedBounds);
        if (ctx.getDebugLevel(p) >= 10) {
            g.debugString(
                    "Flow:\n"
                            + "expected=" + bg + "\n"
                            + "fullSize=" + ee.fullSize.toString() + "\n"
                            + "newExpectedBounds=" + newExpectedBounds.toString(),
                    30, 30
            );
        }
        NTxNodeRendererContext ctx2 = ctx.withBounds(p, newExpectedBounds);
        ee = compute(p, newExpectedBounds, ctx2);

        bg = bg.expand(newExpectedBounds);
        if (!ctx.isDry()) {
            ctx.paintBackground(p, bg);
        }

        for (Elem elem : ee.elems) {
            NTxNodeRendererContext ctx3 = ctx.withBounds(p, elem.bounds);
            ctx3.render(elem.node);
        }


        ctx.paintBorderLine(p, bg);
    }
}

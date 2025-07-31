/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.container.stack;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class NDocGroupBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.GROUP)
                .renderComponent(this::renderMain)
        ;
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 selfBounds = ctx.selfBounds(p);
        NDocGraphics g = ctx.graphics();
        NDocNodeRendererContext ctx2 = ctx.withBounds(p, selfBounds);
        if (!ctx2.isDry()) {
            NDocNodeRendererUtils.paintBackground(p, ctx2, g, selfBounds);
        }
        NDocNodeRendererContext finalCtx = ctx;
        List<NDocNode> texts = p.children()
                .stream().filter(x -> NDocValueByName.isVisible(x, finalCtx)).collect(Collectors.toList());
        for (NDocNode text : texts) {
            ctx2.render(text);
        }
        if (!ctx2.isDry()) {
            NDocNodeRendererUtils.paintBorderLine(p, ctx2, g, selfBounds);
        }
    }

}

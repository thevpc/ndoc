/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.container;

import net.thevpc.ntexup.api.document.elem2d.NDocBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class NDocGroupBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.GROUP)
                .renderComponent(this::renderMain)
        ;
    }

    public void renderMain(NTxNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 selfBounds = ctx.selfBounds(p);
        NDocGraphics g = ctx.graphics();
        NDocNodeRendererContext ctx2 = ctx.withBounds(p, selfBounds);
        if (!ctx2.isDry()) {
            ctx2.paintBackground(p, selfBounds);
        }
        NDocNodeRendererContext finalCtx = ctx;
        List<NTxNode> texts = p.children()
                .stream().filter(x -> finalCtx.isVisible(x)).collect(Collectors.toList());
        for (NTxNode text : texts) {
            ctx2.render(text);
        }
        if (!ctx2.isDry()) {
            ctx2.paintBorderLine(p, selfBounds);
        }
    }

}

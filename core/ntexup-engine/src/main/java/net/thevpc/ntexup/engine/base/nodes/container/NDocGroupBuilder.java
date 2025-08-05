/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.container;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class NDocGroupBuilder implements NDocNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.GROUP)
                .renderComponent(this::renderMain)
        ;
    }

    public void renderMain(NTxNode p, NDocNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxBounds2 selfBounds = ctx.selfBounds(p);
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

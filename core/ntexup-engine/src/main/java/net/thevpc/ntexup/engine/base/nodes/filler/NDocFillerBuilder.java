/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.filler;

import net.thevpc.ntexup.api.document.elem2d.NDocBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

/**
 * @author vpc
 */
public class NDocFillerBuilder implements NDocNodeBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.FILLER)
                .renderComponent(this::renderMain);
    }

    public void renderMain(NTxNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext builderContext) {
        NDocGraphics g = ctx.graphics();
        NDocBounds2 bounds = ctx.getBounds();
        NDocBounds2 b = new NDocBounds2(
                bounds.getMinX(),
                bounds.getMinY(),
                bounds.getWidth(),
                bounds.getHeight());
        if (!ctx.isDry()) {
            ctx.paintBackground(p, bounds);
            ctx.paintBorderLine(p, b);
        }
        ctx.paintDebugBox(p, b);
    }

}

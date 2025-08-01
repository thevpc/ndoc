/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.filler;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

/**
 * @author vpc
 */
public class NDocFillerBuilder implements NDocNodeCustomBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.FILLER)
                .renderComponent(this::renderMain);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
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

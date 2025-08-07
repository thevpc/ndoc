/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.container;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.*;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.engine.base.nodes.container.util.NTxListHelper;

import java.util.List;

/**
 * @author vpc
 */
public class NTxUnorderedListBuilder implements NTxNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.UNORDERED_LIST)
                .alias("ul")
                .selfBounds(this::selfBounds)
                .renderComponent(this::renderMain)
        ;
    }

    public NTxBounds2 selfBounds(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        List<NTxListHelper.NodeWithIndent> all = NTxListHelper.build(p, false,ctx, builderContext);
        NTxBounds2 expectedBounds = ctx.defaultSelfBounds(p);
        for (NTxListHelper.NodeWithIndent a : all) {
            expectedBounds.expand(a.rowBounds);
        }
        return expectedBounds;
    }

    public void renderMain(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        //NTxBounds2 expectedBounds = ctx.selfBounds(p);
        List<NTxListHelper.NodeWithIndent> all = NTxListHelper.build(p, false, ctx, builderContext);
        for (int i = 0; i < all.size(); i++) {
            NTxListHelper.NodeWithIndent a = all.get(i);
            NTxNodeRendererContext c = ctx.withBounds(a.bullet, a.bulletBounds);
            c.render(a.bullet);
            c = ctx.withBounds(a.child, a.childBounds);
            c.render(a.child);
        }
    }


}

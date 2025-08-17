/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.container;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.engine.NTxNodeBuilderContext;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.engine.base.nodes.container.util.NTxGridRendererHelper;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class NTxColumnBuilder implements NTxNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeBuilderContext builderContext) {
        builderContext.id(NTxNodeType.COLUMN)
                .alias("col")
                .parseParam().matchesNamedPair(NTxPropName.ROWS).then()
                .parseParam((info, buildContext) -> {
                    NElement p = info.peek();
                    if(p.isInt()) {
                        info.read();
                        info.node().setProperty(NTxProp.of(NTxPropName.ROWS,p));
                        return true;
                    }else{
                        return false;
                    }
                })
                .selfBounds(this::selfBounds)
                .renderComponent(this::renderMain)
                ;
    }


    public NTxBounds2 selfBounds(NTxNodeRendererContext rendererContext, NTxNodeBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(defaultStyles);
        NTxNode node = rendererContext.node();
        NTxBounds2 expectedBounds = rendererContext.defaultSelfBounds();
        NTxGridRendererHelper h = new NTxGridRendererHelper(node.children());
        return h.computeBound(node, rendererContext, expectedBounds);
    }

    public void renderMain(NTxNodeRendererContext ctx, NTxNodeBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(defaultStyles);
        NTxBounds2 expectedBounds = ctx.selfBounds();
        NTxGridRendererHelper h = new NTxGridRendererHelper(ctx.node().children());
        h.render(ctx, expectedBounds);
    }


//    @Override
//    protected void processImplicitStyles(NTxArgumentParseInfo info) {
//        switch (info.getUid()) {
//            case "vgrid":
//            case "column": {
//                if(info.node().getProperty(NTxPropName.COLUMNS).isNotPresent()) {
//                    info.node().setProperty(NTxPropName.COLUMNS, NElement.ofInt(1));
//                }
//                if(info.node().getProperty(NTxPropName.ROWS).isNotPresent()) {
//                    info.node().setProperty(NTxPropName.ROWS, NElement.ofInt(-1));
//                }
//                break;
//            }
//            case "hgrid":
//            case "row": {
//                if(info.node().getProperty(NTxPropName.COLUMNS).isNotPresent()) {
//                    info.node().setProperty(NTxPropName.COLUMNS, NElement.ofInt(-1));
//                }
//                if(info.node().getProperty(NTxPropName.ROWS).isNotPresent()) {
//                    info.node().setProperty(NTxPropName.ROWS, NElement.ofInt(1));
//                }
//                break;
//            }
//        }
//    }

}

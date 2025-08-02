/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.base.nodes.container;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class NDocColumnBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.COLUMN)
                .alias("col")
                .parseParam().named(NDocPropName.ROWS).then()
                .parseParam((info, buildContext) -> {
                    NElement p = info.peek();
                    if(p.isInt()) {
                        info.read();
                        info.node().setProperty(NDocProp.of(NDocPropName.ROWS,p));
                        return true;
                    }else{
                        return false;
                    }
                })
                .selfBounds(this::selfBounds)
                .renderComponent(this::renderMain)
                ;
    }


    public NDocBounds2 selfBounds(NDocNode p, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 expectedBounds = ctx.defaultSelfBounds(p);
        NDocGridRendererHelper h = new NDocGridRendererHelper(p.children());
        return h.computeBound(p, ctx, expectedBounds);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 expectedBounds = ctx.selfBounds(p);
        NDocGridRendererHelper h = new NDocGridRendererHelper(p.children());
        h.render(p, ctx, expectedBounds);
    }


//    @Override
//    protected void processImplicitStyles(NDocArgumentParseInfo info) {
//        switch (info.getUid()) {
//            case "vgrid":
//            case "column": {
//                if(info.node().getProperty(NDocPropName.COLUMNS).isNotPresent()) {
//                    info.node().setProperty(NDocPropName.COLUMNS, NElement.ofInt(1));
//                }
//                if(info.node().getProperty(NDocPropName.ROWS).isNotPresent()) {
//                    info.node().setProperty(NDocPropName.ROWS, NElement.ofInt(-1));
//                }
//                break;
//            }
//            case "hgrid":
//            case "row": {
//                if(info.node().getProperty(NDocPropName.COLUMNS).isNotPresent()) {
//                    info.node().setProperty(NDocPropName.COLUMNS, NElement.ofInt(-1));
//                }
//                if(info.node().getProperty(NDocPropName.ROWS).isNotPresent()) {
//                    info.node().setProperty(NDocPropName.ROWS, NElement.ofInt(1));
//                }
//                break;
//            }
//        }
//    }

}

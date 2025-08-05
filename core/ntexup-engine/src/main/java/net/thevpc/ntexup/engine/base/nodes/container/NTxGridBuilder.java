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
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.parser.NTxAllArgumentReader;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class NTxGridBuilder implements NTxNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.GRID)
                .parseParam().named(NTxPropName.COLUMNS, NTxPropName.ROWS).then()
                .parseParam((info, buildContext) -> {
                    NElement p = info.peek();
                    if (p.isInt()) {
                        if (info.node().getPropertyValue(NTxPropName.COLUMNS).isEmpty()) {
                            info.read();
                            info.node().setProperty(NTxProp.of(NTxPropName.COLUMNS, p));
                        } else if (info.node().getPropertyValue(NTxPropName.ROWS).isEmpty()) {
                            info.read();
                            info.node().setProperty(NTxProp.of(NTxPropName.ROWS, p));
                        } else {
                            info.read();
                            //just ignore
                        }
                        return true;
                    } else if (p.isUplet() && p.asUplet().get().size() == 2 && p.asUplet().get().get(0).get().isInt() && p.asUplet().get().get(1).get().isInt()) {
                        info.read();
                        if (info.node().getPropertyValue(NTxPropName.COLUMNS).isEmpty()) {
                            info.node().setProperty(NTxProp.of(NTxPropName.COLUMNS, p.asUplet().get().get(0).get()));
                        }
                        if (info.node().getPropertyValue(NTxPropName.ROWS).isEmpty()) {
                            info.node().setProperty(NTxProp.of(NTxPropName.ROWS, p.asUplet().get().get(1).get()));
                        }
                        return true;
                    } else {
                        return false;
                    }
                })
                .afterParsingAllParams(new NTxNodeCustomBuilderContext.ProcessNodeAction() {
                    @Override
                    public void processNode(NTxAllArgumentReader info, NTxNodeCustomBuilderContext buildContext) {
                        if (info.node().getPropertyValue(NTxPropName.COLUMNS).isEmpty() && info.node().getPropertyValue(NTxPropName.ROWS).isEmpty()) {
                            info.node().setProperty(NTxProp.ofInt(NTxPropName.COLUMNS, 2));
                            info.node().setProperty(NTxProp.ofInt(NTxPropName.ROWS, 2));
                        } else if (info.node().getPropertyValue(NTxPropName.COLUMNS).isEmpty()) {
                            info.node().setProperty(NTxProp.of(NTxPropName.COLUMNS, info.node().getPropertyValue(NTxPropName.ROWS).get()));
                        } else if (info.node().getPropertyValue(NTxPropName.ROWS).isEmpty()) {
                            info.node().setProperty(NTxProp.of(NTxPropName.ROWS, info.node().getPropertyValue(NTxPropName.COLUMNS).get()));
                        }
                    }
                })
                .selfBounds(this::selfBounds)
                .renderComponent(this::renderMain)
        ;
    }


    public NTxBounds2 selfBounds(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxBounds2 expectedBounds = ctx.defaultSelfBounds(p);
//        HGraphics g = ctx.graphics();
//        g.setColor(Color.RED);
//        g.drawRect(expectedBounds);
        NTxGridRendererHelper h = new NTxGridRendererHelper(p.children());
        return h.computeBound(p, ctx, expectedBounds);
    }

    public void renderMain(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxBounds2 expectedBounds = ctx.selfBounds(p);
        NTxGridRendererHelper h = new NTxGridRendererHelper(p.children());
        h.render(p, ctx, expectedBounds);
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

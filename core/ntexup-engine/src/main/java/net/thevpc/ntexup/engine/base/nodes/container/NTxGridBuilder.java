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
import net.thevpc.ntexup.api.parser.NTxAllArgumentReader;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.engine.base.nodes.container.util.NTxGridRendererHelper;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class NTxGridBuilder implements NTxNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeBuilderContext builderContext) {
        builderContext.id(NTxNodeType.GRID)
                .parseParam().matchesNamedPair(NTxPropName.COLUMNS, NTxPropName.ROWS).then()
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
                .afterParsingAllParams(new NTxNodeBuilderContext.ProcessNodeAction() {
                    @Override
                    public void processNode(NTxAllArgumentReader info, NTxNodeBuilderContext buildContext) {
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
                .selfBounds((ctx, builderContext1) -> selfBounds(ctx, builderContext1))
                .renderComponent(this::renderMain)
        ;
    }


    public NTxBounds2 selfBounds(NTxNodeRendererContext rendererContext, NTxNodeBuilderContext builderContext) {
        NTxNode node = rendererContext.node();
        rendererContext = rendererContext.withDefaultStyles(defaultStyles);
        NTxBounds2 expectedBounds = rendererContext.defaultSelfBounds();
//        HGraphics g = ctx.graphics();
//        g.setColor(Color.RED);
//        g.drawRect(expectedBounds);
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

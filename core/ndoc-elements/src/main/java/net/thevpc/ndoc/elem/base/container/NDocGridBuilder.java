/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.container;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocAllArgumentReader;
import net.thevpc.ndoc.api.parser.NDocArgumentReader;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class NDocGridBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.GRID)
                .parseParam().named(NDocPropName.COLUMNS, NDocPropName.ROWS).then()
                .parseParam((info, buildContext) -> {
                    NElement p = info.peek();
                    if (p.isInt()) {
                        if (!info.node().getPropertyValue(NDocPropName.COLUMNS).isEmpty()) {
                            info.read();
                            info.node().setProperty(NDocProp.of(NDocPropName.COLUMNS, p));
                        } else if (!info.node().getPropertyValue(NDocPropName.ROWS).isEmpty()) {
                            info.read();
                            info.node().setProperty(NDocProp.of(NDocPropName.ROWS, p));
                        } else {
                            info.read();
                            //just ignore
                        }
                        return true;
                    } else if (p.isUplet() && p.asUplet().get().size() == 2 && p.asUplet().get().get(0).get().isInt() && p.asUplet().get().get(1).get().isInt()) {
                        info.read();
                        if (!info.node().getPropertyValue(NDocPropName.COLUMNS).isEmpty()) {
                            info.node().setProperty(NDocProp.of(NDocPropName.COLUMNS, p.asUplet().get().get(0).get()));
                        }
                        if (!info.node().getPropertyValue(NDocPropName.ROWS).isEmpty()) {
                            info.node().setProperty(NDocProp.of(NDocPropName.ROWS, p.asUplet().get().get(1).get()));
                        }
                        return true;
                    } else {
                        return false;
                    }
                })
                .afterParsingAllParams(new NDocNodeCustomBuilderContext.ProcessNodeAction() {
                    @Override
                    public void processNode(NDocAllArgumentReader info, NDocNodeCustomBuilderContext buildContext) {
                        if (info.node().getPropertyValue(NDocPropName.COLUMNS).isEmpty() && info.node().getPropertyValue(NDocPropName.ROWS).isEmpty()) {
                            info.node().setProperty(NDocProp.ofInt(NDocPropName.COLUMNS, 2));
                            info.node().setProperty(NDocProp.ofInt(NDocPropName.ROWS, 2));
                        } else if (info.node().getPropertyValue(NDocPropName.COLUMNS).isEmpty()) {
                            info.node().setProperty(NDocProp.of(NDocPropName.COLUMNS, info.node().getPropertyValue(NDocPropName.ROWS).get()));
                        } else if (info.node().getPropertyValue(NDocPropName.ROWS).isEmpty()) {
                            info.node().setProperty(NDocProp.of(NDocPropName.ROWS, info.node().getPropertyValue(NDocPropName.COLUMNS).get()));
                        }
                    }
                })
                .selfBounds(this::selfBounds)
                .renderComponent(this::renderMain)
        ;
    }


    public NDocBounds2 selfBounds(NDocNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocBounds2 expectedBounds = ctx.defaultSelfBounds(p);
//        HGraphics g = ctx.graphics();
//        g.setColor(Color.RED);
//        g.drawRect(expectedBounds);
        NDocGridRendererHelper h = new NDocGridRendererHelper(p.children());
        return h.computeBound(p, ctx, expectedBounds);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext builderContext) {
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

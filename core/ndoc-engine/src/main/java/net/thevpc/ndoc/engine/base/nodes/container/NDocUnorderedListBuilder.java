/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.container;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.elem2d.NDocAlign;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class NDocUnorderedListBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();
    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.UNORDERED_LIST)
                .alias("ul")
                .renderConvert(this::convert)
                ;
    }

    private NDocNode convert(NDocNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext buildContext){
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocDocumentFactory f = ctx.documentFactory();
        List<NDocNode> all = new ArrayList<>();
        for (NDocNode child : p.children()) {
            switch (child.type()) {
                case NDocNodeType.UNORDERED_LIST:
                case NDocNodeType.ORDERED_LIST: {
                    all.add(f.ofVoid().addStyleClasses("ul-bullet").setSource(p.source()));
                    break;
                }
                default: {
                    all.add(f.ofSphere()
                            //.setProperty(HPropName.SIZE, NElements.ofDouble(2,"%P"))
                            .addStyleClasses("ul-bullet")
                            .setSource(p.source())
                    );
                    break;
                }
            }
            all.add(child.addStyleClasses("ul-item").setSource(p.source()));
        }
        NDocNode newNode = f.ofGrid().addChildren(all.toArray(new NDocNode[0]))
                .setProperty(NDocPropName.COLUMNS, NElement.ofInt(2))
                .setProperty(NDocPropName.ROWS, NElement.ofInt(2))
                .setProperty(NDocPropName.ORIGIN, NDocAlign.TOP_LEFT)
                .setProperty(NDocPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 20))
                .setProperties(p.props().toArray(new NDocProp[0]));
        for (String s : new String[]{
                NDocPropName.GRID_COLOR
                , NDocPropName.ORIGIN
                , NDocPropName.DRAW_GRID
                , NDocPropName.DRAW_CONTOUR
                , NDocPropName.COLUMNS_WEIGHT
                , NDocPropName.BACKGROUND_COLOR
                , NDocPropName.FILL_BACKGROUND
                , NDocPropName.PADDING
                , NDocPropName.MARGIN
                , NDocPropName.AT
                , NDocPropName.STROKE
                , NDocPropName.CLASS
                , NDocPropName.DEBUG
                , NDocPropName.DEBUG_COLOR
                , NDocPropName.HIDE
                , NDocPropName.FOREGROUND_COLOR
                , NDocPropName.FONT_BOLD
                , NDocPropName.FONT_FAMILY
                , NDocPropName.FONT_ITALIC
                , NDocPropName.FONT_SIZE
                , NDocPropName.FONT_UNDERLINED
                , NDocPropName.FONT_STRIKE
                , NDocPropName.FONT_STRIKE
        }) {
            NElement v = ctx.computePropertyValue(p, s).orNull();
            if (v != null) {
                newNode.setProperty(s, v);
            }
        }
        if(ctx.isDebug(p)){
            Object v = ctx.computePropertyValue(p, NDocPropName.DRAW_GRID).orNull();
            if(v==null){
                newNode.setProperty(NDocPropName.DRAW_GRID, NElement.ofBoolean(true));
            }
        }
        return newNode;
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.container;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.elem2d.NTxAlign;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
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
                .renderConvert(this::convert)
                ;
    }

    private NTxNode convert(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext buildContext){
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxDocumentFactory f = ctx.documentFactory();
        List<NTxNode> all = new ArrayList<>();
        for (NTxNode child : p.children()) {
            switch (child.type()) {
                case NTxNodeType.UNORDERED_LIST:
                case NTxNodeType.ORDERED_LIST: {
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
        NTxNode newNode = f.ofGrid().addChildren(all.toArray(new NTxNode[0]))
                .setProperty(NTxPropName.COLUMNS, NElement.ofInt(2))
                .setProperty(NTxPropName.ROWS, NElement.ofInt(2))
                .setProperty(NTxPropName.ORIGIN, NTxAlign.TOP_LEFT)
                .setProperty(NTxPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 20))
                .setProperties(p.props().toArray(new NTxProp[0]));
        for (String s : new String[]{
                NTxPropName.GRID_COLOR
                , NTxPropName.ORIGIN
                , NTxPropName.DRAW_GRID
                , NTxPropName.DRAW_CONTOUR
                , NTxPropName.COLUMNS_WEIGHT
                , NTxPropName.BACKGROUND_COLOR
                , NTxPropName.FILL_BACKGROUND
                , NTxPropName.PADDING
                , NTxPropName.MARGIN
                , NTxPropName.AT
                , NTxPropName.STROKE
                , NTxPropName.CLASS
                , NTxPropName.DEBUG
                , NTxPropName.DEBUG_COLOR
                , NTxPropName.HIDE
                , NTxPropName.FOREGROUND_COLOR
                , NTxPropName.FONT_BOLD
                , NTxPropName.FONT_FAMILY
                , NTxPropName.FONT_ITALIC
                , NTxPropName.FONT_SIZE
                , NTxPropName.FONT_UNDERLINED
                , NTxPropName.FONT_STRIKE
                , NTxPropName.FONT_STRIKE
        }) {
            NElement v = ctx.computePropertyValue(p, s).orNull();
            if (v != null) {
                newNode.setProperty(s, v);
            }
        }
        if(ctx.isDebug(p)){
            Object v = ctx.computePropertyValue(p, NTxPropName.DRAW_GRID).orNull();
            if(v==null){
                newNode.setProperty(NTxPropName.DRAW_GRID, NElement.ofBoolean(true));
            }
        }
        return newNode;
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.container;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.elem2d.NDocAlign;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.document.style.NDocPropName;
import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class NDocUnorderedListBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();
    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.UNORDERED_LIST)
                .alias("ul")
                .renderConvert(this::convert)
                ;
    }

    private NTxNode convert(NTxNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext buildContext){
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxDocumentFactory f = ctx.documentFactory();
        List<NTxNode> all = new ArrayList<>();
        for (NTxNode child : p.children()) {
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
        NTxNode newNode = f.ofGrid().addChildren(all.toArray(new NTxNode[0]))
                .setProperty(NDocPropName.COLUMNS, NElement.ofInt(2))
                .setProperty(NDocPropName.ROWS, NElement.ofInt(2))
                .setProperty(NDocPropName.ORIGIN, NDocAlign.TOP_LEFT)
                .setProperty(NDocPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 20))
                .setProperties(p.props().toArray(new NTxProp[0]));
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

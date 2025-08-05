/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.container;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class NDocOrderedListBuilder implements NDocNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();
    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.ORDERED_LIST)
                .alias("ol")
                .renderConvert(this::convert)
                ;
    }

    private NTxNode convert(NTxNode p, NDocNodeRendererContext ctx, NTxNodeCustomBuilderContext buildContext){
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxDocumentFactory f = ctx.documentFactory();
        List<NTxNode> all = new ArrayList<>();
        for (NTxNode child : p.children()) {
            all.add(f.of(NTxNodeType.SPHERE).addStyleClasses("ol-bullet"));
            all.add(child.addStyleClasses("ol-item"));
        }
        return f.ofGrid().addChildren(all.toArray(new NTxNode[0]))
                .setProperty(NTxPropName.COLUMNS, NElement.ofInt(2))
                .setProperty(NTxPropName.ROWS, NElement.ofInt(2))
                .setProperty(NTxPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 20))
                .setProperties(p.props().toArray(new NTxProp[0]))
                ;
    }

}

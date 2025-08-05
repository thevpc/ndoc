/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.container;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
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
public class NDocOrderedListBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();
    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.ORDERED_LIST)
                .alias("ol")
                .renderConvert(this::convert)
                ;
    }

    private NTxNode convert(NTxNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext buildContext){
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxDocumentFactory f = ctx.documentFactory();
        List<NTxNode> all = new ArrayList<>();
        for (NTxNode child : p.children()) {
            all.add(f.of(NDocNodeType.SPHERE).addStyleClasses("ol-bullet"));
            all.add(child.addStyleClasses("ol-item"));
        }
        return f.ofGrid().addChildren(all.toArray(new NTxNode[0]))
                .setProperty(NDocPropName.COLUMNS, NElement.ofInt(2))
                .setProperty(NDocPropName.ROWS, NElement.ofInt(2))
                .setProperty(NDocPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 20))
                .setProperties(p.props().toArray(new NTxProp[0]))
                ;
    }

}

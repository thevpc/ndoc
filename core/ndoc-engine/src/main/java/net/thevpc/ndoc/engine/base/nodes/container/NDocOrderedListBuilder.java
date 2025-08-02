/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.container;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class NDocOrderedListBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();
    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.ORDERED_LIST)
                .alias("ol")
                .renderConvert(this::convert)
                ;
    }

    private NDocNode convert(NDocNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext buildContext){
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocDocumentFactory f = ctx.documentFactory();
        List<NDocNode> all = new ArrayList<>();
        for (NDocNode child : p.children()) {
            all.add(f.of(NDocNodeType.SPHERE).addStyleClasses("ol-bullet"));
            all.add(child.addStyleClasses("ol-item"));
        }
        return f.ofGrid().addChildren(all.toArray(new NDocNode[0]))
                .setProperty(NDocPropName.COLUMNS, NElement.ofInt(2))
                .setProperty(NDocPropName.ROWS, NElement.ofInt(2))
                .setProperty(NDocPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 20))
                .setProperties(p.props().toArray(new NDocProp[0]))
                ;
    }

}

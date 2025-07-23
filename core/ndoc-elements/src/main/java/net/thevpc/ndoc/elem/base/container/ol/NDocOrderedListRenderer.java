package net.thevpc.ndoc.elem.base.container.ol;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocProp;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.base.renderer.ConvertedNDocNodeRenderer;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

public class NDocOrderedListRenderer extends ConvertedNDocNodeRenderer {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocOrderedListRenderer() {
        super(NDocNodeType.ORDERED_LIST);
    }

    public NDocNode convert(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocDocumentFactory f = ctx.documentFactory();
        List<NDocNode> all = new ArrayList<>();
        for (NDocNode child : p.children()) {
            all.add(f.of(NDocNodeType.SPHERE).addStyleClasses("ol-bullet"));
            all.add(child.addStyleClasses("ol-item"));
        }
        return f.ofGrid().addAll(all.toArray(new NDocNode[0]))
                .setProperty(NDocPropName.COLUMNS, NElement.ofInt(2))
                .setProperty(NDocPropName.ROWS, NElement.ofInt(2))
                .setProperty(NDocPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 20))
                .setProperties(p.props().toArray(new NDocProp[0]))
                ;
    }
}

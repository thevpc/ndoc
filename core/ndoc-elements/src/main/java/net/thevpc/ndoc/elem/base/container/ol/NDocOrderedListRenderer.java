package net.thevpc.ndoc.elem.base.container.ol;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.base.renderer.ConvertedNDocNodeRenderer;
import net.thevpc.nuts.elem.NElements;

import java.util.ArrayList;
import java.util.List;

public class NDocOrderedListRenderer extends ConvertedNDocNodeRenderer {
    HProperties defaultStyles = new HProperties();

    public NDocOrderedListRenderer() {
        super(HNodeType.ORDERED_LIST);
    }

    public HNode convert(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocDocumentFactory f = ctx.documentFactory();
        List<HNode> all = new ArrayList<>();
        for (HNode child : p.children()) {
            all.add(f.of(HNodeType.SPHERE).addStyleClasses("ol-bullet"));
            all.add(child.addStyleClasses("ol-item"));
        }
        return f.ofGrid().addAll(all.toArray(new HNode[0]))
                .setProperty(HPropName.COLUMNS, NElements.ofInt(2))
                .setProperty(HPropName.ROWS, NElements.ofInt(2))
                .setProperty(HPropName.COLUMNS_WEIGHT,NElements.ofDoubleArray(1, 20))
                .setProperties(p.props().toArray(new HProp[0]))
                ;
    }
}

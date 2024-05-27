package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProps;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.ConvertedHPartRenderer;

import java.util.ArrayList;
import java.util.List;

public class HOrderedListRenderer extends ConvertedHPartRenderer {
    HProperties defaultStyles = new HProperties();

    public HOrderedListRenderer() {
        super(HNodeType.ORDERED_LIST);
    }

    public HNode convert(HNode p, HNodeRendererContext ctx){
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        HDocumentFactory f = ctx.documentFactory();
        List<HNode> all = new ArrayList<>();
        for (HNode child : p.children()) {
            all.add(f.of(HNodeType.SPHERE).addStyleClasses("ol-bullet"));
            all.add(child.addStyleClasses("ol-item"));
        }
        return f.ofGrid().addAll(all.toArray(new HNode[0]))
                .setProperty(HProp.ofInt(HPropName.COLUMNS,2))
                .setProperty(HProp.ofInt(HPropName.ROWS,2))
                .setProperty(HProps.columnsWeight(1, 20))
                .setProperties(p.props().toArray(new HProp[0]))
                ;
    }
}

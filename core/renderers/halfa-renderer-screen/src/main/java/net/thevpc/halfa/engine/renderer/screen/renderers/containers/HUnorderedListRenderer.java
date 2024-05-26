package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProps;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.ConvertedHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;

import java.util.ArrayList;
import java.util.List;

public class HUnorderedListRenderer extends ConvertedHPartRenderer {
    HProperties defaultStyles = new HProperties();

    public HUnorderedListRenderer() {
        super(HNodeType.UNORDERED_LIST);
    }

    public HNode convert(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        HContainer ul = (HContainer) p;
        HDocumentFactory f = ctx.documentFactory();
        List<HNode> all = new ArrayList<>();
        for (HNode child : ul.children()) {
            switch (child.type()){
                case HNodeType.UNORDERED_LIST:
                case HNodeType.ORDERED_LIST:{
                    all.add(f.ofVoid().addClasses("ul-bullet"));
                    break;
                }
                default:{
                    all.add(f.ofSphere()
                                    .setProperty(HProps.size(30))
                            .addClasses("ul-bullet"));
                    break;
                }
            }
            all.add(child.addClasses("ul-item"));
        }
        return f.ofGrid().addAll(all.toArray(new HNode[0]))
                .setProperty(HProp.ofInt(HPropName.COLUMNS,2))
                .setProperty(HProp.ofInt(HPropName.ROWS,2))
                .setProperty(HProps.columnsWeight(1, 20))
                .set(ul.props().toArray(new HProp[0]))
                ;
    }
}

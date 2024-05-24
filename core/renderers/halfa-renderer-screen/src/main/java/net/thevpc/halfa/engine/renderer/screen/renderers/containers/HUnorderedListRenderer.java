package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.container.HUnorderedList;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.ConvertedHPartRenderer;

import java.util.ArrayList;
import java.util.List;

public class HUnorderedListRenderer extends ConvertedHPartRenderer {
    public HNode convert(HNode p, HPartRendererContext ctx) {
        HUnorderedList ul = (HUnorderedList) p;
        HDocumentFactory f = ctx.documentFactory();
        List<HNode> all = new ArrayList<>();
        for (HNode child : ul.children()) {
            switch (child.type()){
                case UNORDERED_LIST:
                case ORDERED_LIST:{
                    all.add(f.ofVoid().addClasses("ul-bullet"));
                    break;
                }
                default:{
                    all.add(f.ofCircle(30).addClasses("ul-bullet"));
                    break;
                }
            }
            all.add(child.addClasses("ul-item"));
        }
        return f.ofGrid(2, ul.children().size(),
                        all.toArray(new HNode[0])
                ).set(HStyles.columnsWeight(1, 20))
                .set(ul.styles().toArray(new HStyle[0]))
                ;
    }
}

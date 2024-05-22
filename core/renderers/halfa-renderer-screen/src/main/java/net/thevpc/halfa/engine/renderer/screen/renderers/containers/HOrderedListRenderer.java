package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HOrderedList;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.ConvertedHPartRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HOrderedListRenderer extends ConvertedHPartRenderer {
    public HNode convert(HNode p, HPartRendererContext ctx){
        HOrderedList ul = (HOrderedList) p;
        HDocumentFactory f = ctx.documentFactory();
        List<HNode> all = new ArrayList<>();
        for (HNode child : ul.children()) {
            all.add(f.circle(50)
                    .set(HStyles.backgroundColor(Color.GREEN))
                    .set(HStyles.lineColor(Color.GREEN.darker()))
            );
            all.add(child);
        }
        return f.grid(2, ul.children().size(),
                all.toArray(new HNode[0])
        ).set(HStyles.columnsWeight(1, 20))
                ;
    }
}

package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.node.container.HContainer;
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
            all.add(f.circle(50)
                            .addClasses("ul-bullet")
            );
            all.add(child
                    .addClasses("ul-item")
                    );
//            all.add(f.rectangle().set(HStyles.foregroundColor(Color.BLUE)).set(HStyles.anchor(HAnchor.LEFT)));
        }
        HContainer t = f.grid(2, ul.children().size(),
                        all.toArray(new HNode[0])
                ).set(HStyles.columnsWeight(1, 20))
                .set(HStyles.origin(HAlign.TOP));
        return t;
    }
}

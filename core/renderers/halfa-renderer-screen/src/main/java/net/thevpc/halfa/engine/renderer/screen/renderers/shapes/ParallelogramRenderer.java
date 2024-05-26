package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public class ParallelogramRenderer extends HPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public ParallelogramRenderer() {
        super(HNodeType.PARALLELOGRAM);
    }

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        double w=20;
        Double2[] points =new Double2[]{
                new Double2(0,100),
                new Double2(100-w,100),
                new Double2(100,0),
                new Double2(w,0),
        };
        return render(p,points,ctx);
    }

}

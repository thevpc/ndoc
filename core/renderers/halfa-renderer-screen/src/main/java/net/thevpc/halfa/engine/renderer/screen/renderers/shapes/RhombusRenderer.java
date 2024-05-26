package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public class RhombusRenderer extends HPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public RhombusRenderer() {
        super(HNodeType.RHOMBUS);
    }

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Double2[] points =new Double2[]{
                new Double2(0,50),
                new Double2(50,0),
                new Double2(100,50),
                new Double2(50,100),
        };
        return render(p, points, ctx);
    }

}

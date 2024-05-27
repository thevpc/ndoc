package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public class TrapezoidRenderer extends HPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public TrapezoidRenderer() {
        super(HNodeType.TRAPEZOID);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        double x1 = 20;
        double x2 = 20;
        Double2[] points = new Double2[]{
                new Double2(0, 100),
                new Double2(100, 100),
                new Double2(100 - x2, 0),
                new Double2(x1, 0),
        };
        render(p, points, ctx);
    }

}

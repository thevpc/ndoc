package net.thevpc.halfa.engine.renderer.screen.renderers.shapes;

import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

public class TriangleRenderer extends HPolygonBaseRenderer {
    HProperties defaultStyles = new HProperties();

    public TriangleRenderer() {
        super(HNodeType.TRIANGLE);
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx=ctx.withDefaultStyles(p,defaultStyles);
        Double2[] points =new Double2[]{
                new Double2(0,100),
                new Double2(100,100),
                new Double2(50,0),
        };
        render(p,points,ctx);
    }

}

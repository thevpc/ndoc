package net.thevpc.ndoc.engine.base.nodes.shape;

import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class NDocTrapezoidBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.TRAPEZOID)
                .renderComponent(this::renderMain)
        ;
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        double x1 = 20;
        double x2 = 20;
        NDocPoint2D[] points = new NDocPoint2D[]{
                new NDocPoint2D(0, 100),
                new NDocPoint2D(100, 100),
                new NDocPoint2D(100 - x2, 0),
                new NDocPoint2D(x1, 0),
        };
        NDocPolygonHelper.renderPoints(p, points, rendererContext);
    }
}

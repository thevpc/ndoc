package net.thevpc.ndoc.elem.base.shape.polygon;

import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class NDocParallelogramBuilder implements NDocNodeCustomBuilder {

    private NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.PARALLELOGRAM)
                .renderComponent(this::renderMain);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        double w = 20;
        NDocPoint2D[] points = new NDocPoint2D[]{
                new NDocPoint2D(0, 100),
                new NDocPoint2D(100 - w, 100),
                new NDocPoint2D(100, 0),
                new NDocPoint2D(w, 0),
        };
        NDocPolygonHelper.renderPoints(p, points, rendererContext);
    }
}

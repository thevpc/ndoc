package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.elem2d.NDocPoint2D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

public class NDocParallelogramBuilder implements NDocNodeBuilder {

    private NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.PARALLELOGRAM)
                .renderComponent(this::renderMain);
    }

    public void renderMain(NTxNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext builderContext) {
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

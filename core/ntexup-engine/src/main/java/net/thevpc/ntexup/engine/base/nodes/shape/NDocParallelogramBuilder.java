package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

public class NDocParallelogramBuilder implements NDocNodeBuilder {

    private NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.PARALLELOGRAM)
                .renderComponent(this::renderMain);
    }

    public void renderMain(NTxNode p, NDocNodeRendererContext rendererContext, NTxNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        double w = 20;
        NTxPoint2D[] points = new NTxPoint2D[]{
                new NTxPoint2D(0, 100),
                new NTxPoint2D(100 - w, 100),
                new NTxPoint2D(100, 0),
                new NTxPoint2D(w, 0),
        };
        NDocPolygonHelper.renderPoints(p, points, rendererContext);
    }
}

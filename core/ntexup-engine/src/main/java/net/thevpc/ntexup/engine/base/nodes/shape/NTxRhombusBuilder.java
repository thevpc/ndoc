package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

public class NTxRhombusBuilder implements NTxNodeBuilder {
    private NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.RHOMBUS)
                .alias("diamond")
                .renderComponent(this::renderMain);
    }

    public void renderMain(NTxNode p, NTxNodeRendererContext rendererContext, NTxNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        NTxPoint2D[] points = new NTxPoint2D[]{
                new NTxPoint2D(0, 50),
                new NTxPoint2D(50, 0),
                new NTxPoint2D(100, 50),
                new NTxPoint2D(50, 100),
        };
        NTxPolygonHelper.renderPoints(p, points, rendererContext);
    }
}

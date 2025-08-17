package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.engine.NTxNodeBuilderContext;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

public class NTxOctagonBuilder implements NTxNodeBuilder {

    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeBuilderContext builderContext) {
        builderContext.id(NTxNodeType.OCTAGON)
                .renderComponent(this::render);
    }

    public void render(NTxNodeRendererContext rendererContext, NTxNodeBuilderContext buildContext) {
        NTxPolygonHelper.renderPointsCount(8, rendererContext, defaultStyles);
    }
}

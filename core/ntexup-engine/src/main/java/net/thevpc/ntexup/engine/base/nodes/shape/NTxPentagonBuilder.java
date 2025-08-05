package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

public class NTxPentagonBuilder implements NTxNodeBuilder {

    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.PENTAGON)
                .renderComponent(this::render);
    }

    public void render(NTxNode p, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext) {
        NTxPolygonHelper.renderPointsCount(5, p, renderContext, defaultStyles);
    }

}

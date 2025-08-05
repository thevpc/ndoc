package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

public class NDocOctagonBuilder implements NDocNodeBuilder {

    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.OCTAGON)
                .renderComponent(this::render);
    }

    public void render(NTxNode p, NDocNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext) {
        NDocPolygonHelper.renderPointsCount(8, p, renderContext, defaultStyles);
    }
}

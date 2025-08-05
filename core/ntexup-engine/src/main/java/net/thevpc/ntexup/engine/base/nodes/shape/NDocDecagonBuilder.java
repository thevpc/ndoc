package net.thevpc.ntexup.engine.base.nodes.shape;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

public class NDocDecagonBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.DECAGON)
                .renderComponent(this::render);
    }

    public void render(NTxNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext buildContext) {
        NDocPolygonHelper.renderPointsCount(10, p, renderContext, defaultStyles);
    }

}

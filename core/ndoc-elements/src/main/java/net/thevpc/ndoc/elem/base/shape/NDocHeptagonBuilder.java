package net.thevpc.ndoc.elem.base.shape;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class NDocHeptagonBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.HEPTAGON)
                .renderComponent(this::render);
    }

    public void render(NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext buildContext) {
        NDocPolygonHelper.renderPointsCount(7, p, renderContext, defaultStyles);
    }

}

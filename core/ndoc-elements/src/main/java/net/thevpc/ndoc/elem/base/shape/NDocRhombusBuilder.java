package net.thevpc.ndoc.elem.base.shape;

import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

public class NDocRhombusBuilder implements NDocNodeCustomBuilder {
    private NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.RHOMBUS)
                .alias("diamond")
                .renderComponent(this::renderMain);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        NDocPoint2D[] points = new NDocPoint2D[]{
                new NDocPoint2D(0, 50),
                new NDocPoint2D(50, 0),
                new NDocPoint2D(100, 50),
                new NDocPoint2D(50, 100),
        };
        NDocPolygonHelper.renderPoints(p, points, rendererContext);
    }
}

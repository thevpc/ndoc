package net.thevpc.ndoc.extension.shapes2d;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;


/**
 *
 */
public class NDocPieBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.PIE)
                .parseParam().named(NDocPropName.START_ANGLE,NDocPropName.EXTENT_ANGLE,NDocPropName.DASH).then()
                .parseParam().named(NDocPropName.SLICE_COUNT).then()
                .parseParam().named(NDocPropName.SLICES).then()
                .parseParam().named(NDocPropName.COLORS).then()
                .renderComponent(this::render)
                ;
    }


    private void render(NDocNode p, NDocNodeRendererContext renderContext,NDocNodeCustomBuilderContext builderContext) {
        NDocDonutBuilder.renderDonutOrPie(p, renderContext, builderContext, defaultStyles, false);
    }



}

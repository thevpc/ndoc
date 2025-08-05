package net.thevpc.ntexup.extension.shapes2d;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.document.style.NDocPropName;
import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;


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


    private void render(NTxNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext builderContext) {
        NDocDonutBuilder.renderDonutOrPie(p, renderContext, builderContext, defaultStyles, false);
    }



}

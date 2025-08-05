package net.thevpc.ntexup.extension.shapes2d;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;


/**
 *
 */
public class NDocPieBuilder implements NDocNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NTxNodeType.PIE)
                .parseParam().named(NTxPropName.START_ANGLE, NTxPropName.EXTENT_ANGLE, NTxPropName.DASH).then()
                .parseParam().named(NTxPropName.SLICE_COUNT).then()
                .parseParam().named(NTxPropName.SLICES).then()
                .parseParam().named(NTxPropName.COLORS).then()
                .renderComponent(this::render)
                ;
    }


    private void render(NTxNode p, NDocNodeRendererContext renderContext, NTxNodeCustomBuilderContext builderContext) {
        NDocDonutBuilder.renderDonutOrPie(p, renderContext, builderContext, defaultStyles, false);
    }



}

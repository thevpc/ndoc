package net.thevpc.ndoc.extension.shapes2d;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.lang.reflect.Field;
import java.util.Arrays;


/**
 *
 */
public class NDocPieBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.PIE)
                .parseAsDouble(NDocPropName.START_ANGLE,NDocPropName.EXTENT_ANGLE,NDocPropName.DASH)
                .parseAsInt(NDocPropName.SLICE_COUNT)
                .parseAsDoubleArray(NDocPropName.SLICES)
                .parseAsStringArray(NDocPropName.COLORS)
                .parseDefaultParamNames()
                .render(this::render)
                ;
    }


    private void render(NDocNode p, NDocNodeRendererContext renderContext,NDocNodeCustomBuilderContext builderContext) {
        NDocDonutBuilder.renderDonutOrPie(p, renderContext, builderContext, defaultStyles, false);
    }



}

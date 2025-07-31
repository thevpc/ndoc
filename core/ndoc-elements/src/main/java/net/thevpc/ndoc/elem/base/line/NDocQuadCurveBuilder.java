package net.thevpc.ndoc.elem.base.line;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocElement2DFactory;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.eval.NDocValueByType;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocObjEx;

import java.awt.*;

public class NDocQuadCurveBuilder implements NDocNodeCustomBuilder {
    NDocProperties defaultStyles = new NDocProperties();


    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.QUAD_CURVE)
                .parseParam().named(NDocPropName.FROM, NDocPropName.TO, NDocPropName.START_ARROW, NDocPropName.END_ARROW, NDocPropName.CTRL).then()
                .renderComponent(this::renderMain)
        ;
    }


    public void renderMain(NDocNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = rendererContext.selfBounds(p);
        NDocPoint2D translation = new NDocPoint2D(b.getX(), b.getY());
        NDocPoint2D from = NDocPoint.ofParent(NDocObjEx.ofProp(p, NDocPropName.FROM).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NDocPoint2D to = NDocPoint.ofParent(NDocObjEx.ofProp(p, NDocPropName.TO).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NDocPoint2D ctrl = NDocPoint.ofParent(NDocObjEx.ofProp(p, NDocPropName.CTRL).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NDocGraphics g = rendererContext.graphics();
        if (!rendererContext.isDry()) {
            Paint fc = NDocValueByName.getForegroundColor(p, rendererContext, true);
            g.draw2D(NDocElement2DFactory.quad(from, ctrl, to)
                    .setStartArrow(NDocValueByType.getArrow(p, rendererContext, NDocPropName.START_ARROW).orNull())
                    .setEndArrow(NDocValueByType.getArrow(p, rendererContext, NDocPropName.END_ARROW).orNull())
                    .setLineStroke(g.createStroke(NDocValueByName.getStroke(p, rendererContext)))
                    .setLinePaint(fc)
            );
        }
        double minx = Math.min(from.getX(), to.getX());
        double miny = Math.min(from.getY(), to.getY());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        NDocBounds2 b2 = new NDocBounds2(minx, miny, maxX, maxY);
        NDocNodeRendererUtils.paintDebugBox(p, rendererContext, g, b2);
    }

}

package net.thevpc.ntexup.engine.base.nodes.line;

import net.thevpc.ntexup.api.document.elem2d.NDocBounds2;
import net.thevpc.ntexup.api.document.elem2d.NDocElement2DFactory;
import net.thevpc.ntexup.api.document.elem2d.NDocPoint;
import net.thevpc.ntexup.api.document.elem2d.NDocPoint2D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.document.style.NDocPropName;
import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.ntexup.api.eval.NDocValueByType;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

import java.awt.*;

public class NDocCubicCurveBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.CUBIC_CURVE)
                .parseParam().named(NDocPropName.FROM, NDocPropName.TO, NDocPropName.START_ARROW, NDocPropName.END_ARROW, NDocPropName.CTRL1, NDocPropName.CTRL2).then()
                .renderComponent(this::renderMain)
        ;
    }

    public void renderMain(NTxNode p, NDocNodeRendererContext rendererContext, NDocNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        NDocBounds2 b = rendererContext.selfBounds(p);
        NDocPoint2D translation = new NDocPoint2D(b.getX(), b.getY());
        NDocPoint2D from = NDocPoint.ofParent(NDocValue.ofProp(p, NDocPropName.FROM).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NDocPoint2D to = NDocPoint.ofParent(NDocValue.ofProp(p, NDocPropName.TO).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NDocPoint2D ctrl1 = NDocPoint.ofParent(NDocValue.ofProp(p, NDocPropName.CTRL1).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NDocPoint2D ctrl2 = NDocPoint.ofParent(NDocValue.ofProp(p, NDocPropName.CTRL2).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NDocGraphics g = rendererContext.graphics();
        if (!rendererContext.isDry()) {
            Paint fc = rendererContext.getForegroundColor(p, true);
            g.draw2D(NDocElement2DFactory.cubic(from, ctrl1, ctrl2, to)
                    .setStartArrow(NDocValueByType.getArrow(p, rendererContext, NDocPropName.START_ARROW).orNull())
                    .setEndArrow(NDocValueByType.getArrow(p, rendererContext, NDocPropName.END_ARROW).orNull())
                    .setLineStroke(g.createStroke(rendererContext.getStroke(p)))
                    .setLinePaint(fc)
            );
        }
        double minx = Math.min(from.getX(), to.getX());
        double miny = Math.min(from.getY(), to.getY());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        NDocBounds2 b2 = new NDocBounds2(minx, miny, maxX, maxY);
        rendererContext.paintDebugBox(p, b2);
    }

}

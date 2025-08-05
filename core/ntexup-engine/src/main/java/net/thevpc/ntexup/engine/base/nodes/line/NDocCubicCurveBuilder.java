package net.thevpc.ntexup.engine.base.nodes.line;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxElement2DFactory;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.ntexup.api.eval.NDocValueByType;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

import java.awt.*;

public class NDocCubicCurveBuilder implements NDocNodeBuilder {
    NTxProperties defaultStyles = new NTxProperties();

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NTxNodeType.CUBIC_CURVE)
                .parseParam().named(NTxPropName.FROM, NTxPropName.TO, NTxPropName.START_ARROW, NTxPropName.END_ARROW, NTxPropName.CTRL1, NTxPropName.CTRL2).then()
                .renderComponent(this::renderMain)
        ;
    }

    public void renderMain(NTxNode p, NDocNodeRendererContext rendererContext, NTxNodeCustomBuilderContext builderContext) {
        rendererContext = rendererContext.withDefaultStyles(p, defaultStyles);
        NTxBounds2 b = rendererContext.selfBounds(p);
        NTxPoint2D translation = new NTxPoint2D(b.getX(), b.getY());
        NTxPoint2D from = NTxPoint.ofParent(NDocValue.ofProp(p, NTxPropName.FROM).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NTxPoint2D to = NTxPoint.ofParent(NDocValue.ofProp(p, NTxPropName.TO).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NTxPoint2D ctrl1 = NTxPoint.ofParent(NDocValue.ofProp(p, NTxPropName.CTRL1).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NTxPoint2D ctrl2 = NTxPoint.ofParent(NDocValue.ofProp(p, NTxPropName.CTRL2).asHPoint2D().get()).valueHPoint2D(b, rendererContext.getGlobalBounds())
                .plus(translation);
        NDocGraphics g = rendererContext.graphics();
        if (!rendererContext.isDry()) {
            Paint fc = rendererContext.getForegroundColor(p, true);
            g.draw2D(NTxElement2DFactory.cubic(from, ctrl1, ctrl2, to)
                    .setStartArrow(NDocValueByType.getArrow(p, rendererContext, NTxPropName.START_ARROW).orNull())
                    .setEndArrow(NDocValueByType.getArrow(p, rendererContext, NTxPropName.END_ARROW).orNull())
                    .setLineStroke(g.createStroke(rendererContext.getStroke(p)))
                    .setLinePaint(fc)
            );
        }
        double minx = Math.min(from.getX(), to.getX());
        double miny = Math.min(from.getY(), to.getY());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        NTxBounds2 b2 = new NTxBounds2(minx, miny, maxX, maxY);
        rendererContext.paintDebugBox(p, b2);
    }

}

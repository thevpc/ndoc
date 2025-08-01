package net.thevpc.ndoc.extension.plot2d;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.MinMax;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NDoubleFunction;

import java.awt.*;


/**
 *
 */
public class NDocPlot2DBuilder implements NDocNodeCustomBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.PLOT2D)
                .parseParam()
                .named(NDocPropName.WIDTH,
                        NDocPropName.HEIGHT,
                        "function"
                ).end()
                .renderComponent(this::renderMain)
                ;
    }

    NDoubleFunction compileFunctionD2(NElement e){
        return x -> Math.sin(x);
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        NDocBounds2 b = ctx.selfBounds(p, null, null);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();
        double minX = -100;
        double maxX = 100;
        double minY = -100;
        double maxY = 100;
        boolean zoom = true;
        MinMax minMaxY = new MinMax();

        Paint color = ctx.getForegroundColor(p, true);

        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {

            g.setPaint(color);
            NDoubleFunction f= compileFunctionD2(p.getPropertyValue("function","fun").orNull());
            NDocBounds2 bounds = ctx.getBounds();
            int steps = (int) (bounds.getY() * 10);
            double[] xx = ArrayUtils.dtimes(minX, maxX, steps);
            double[] yy = new double[xx.length];

            for (int i = 1; i < xx.length; i++) {
                yy[i] = f.apply(xx[i]);
                if (Double.isFinite(yy[i])) {
                    minMaxY.registerValue(yy[i]);
                }
            }
            double yWidth = 0;
            if (zoom) {
                if (Double.isFinite(minMaxY.getMax()) && Double.isFinite(minMaxY.getMin())) {
                    yWidth = minMaxY.getMax() - minMaxY.getMin();
                    minY = minMaxY.getMin() - yWidth * 0.1;
                    maxY = minMaxY.getMax() + yWidth * 0.1;
                } else {
                    minY = -100;
                    maxY = +100;
                }
            }
            DrawContext drawContext = new DrawContext();
            drawContext.componentWidth = bounds.getWidth();
            drawContext.componentHeight = bounds.getHeight();
            drawContext.gridMinX = minX;
            drawContext.gridMaxX = maxX;
            drawContext.gridMinY = minY;
            drawContext.gridMaxY = maxY;
            drawContext.gridWidth = maxX - minX;
            drawContext.gridHeight = maxY - minY;
            if (Double.isFinite(yy[0])) {
                // ??
            }
            for (int i = 1; i < xx.length; i++) {
                if (Double.isFinite(yy[i])) {
                    if (Double.isFinite(yy[i - 1])) {
                        int fromX = (int) drawContext.xPixels(xx[i - 1]);
                        int fromY = (int) drawContext.yPixels(yy[i - 1]);
                        int toX = (int) drawContext.xPixels(xx[i]);
                        int toY = (int) drawContext.yPixels(yy[i]);
                        ctx.graphics().drawLine(
                                fromX, fromY,
                                toX, toY);
                    }
                }
            }
            ctx.paintDebugBox(p, b);
        }
    }

    private static class DrawContext {
        double gridMinX;
        double gridMaxX;
        double gridMinY;
        double gridMaxY;
        double gridWidth;
        double gridHeight;
        double componentWidth;
        double componentHeight;

        public double xPixels(double a) {
            return (a - gridMinX) * componentWidth / gridWidth;
        }

        public double yPixels(double a) {
            return (gridHeight - (a - gridMinY)) * componentHeight / gridHeight;
        }

        public double wPixels(double a) {
            return a * componentWidth / gridWidth;
        }

        public double hPixels(double a) {
            return a * componentHeight / gridHeight;
        }

    }
}

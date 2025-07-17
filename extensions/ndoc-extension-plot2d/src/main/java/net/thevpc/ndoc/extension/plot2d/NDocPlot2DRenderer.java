package net.thevpc.ndoc.extension.plot2d;

import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.util.MinMax;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;

import java.awt.*;

public class NDocPlot2DRenderer extends NDocNodeRendererBase {
    DoubleToDoubleFunction f = x -> Math.sin(x);

    public NDocPlot2DRenderer() {
        super(NDocNodeType.PLOT2D);
    }

    @Override
    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        NDocBounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
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

        Paint color = NDocValueByName.getForegroundColor(p, ctx, true);

        NDocGraphics g = ctx.graphics();
        if (!ctx.isDry()) {
            g.setPaint(color);

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
            NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
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

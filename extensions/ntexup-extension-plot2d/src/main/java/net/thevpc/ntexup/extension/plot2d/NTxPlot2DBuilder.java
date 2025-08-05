package net.thevpc.ntexup.extension.plot2d;

import net.thevpc.ntexup.api.document.NTxArrow;
import net.thevpc.ntexup.api.document.NTxArrowType;
import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;
import net.thevpc.ntexup.api.document.elem2d.NTxVector2D;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.parser.NTxAllArgumentReader;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxMinMax;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.NDoubleFunction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class NTxPlot2DBuilder implements NTxNodeBuilder {

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.PLOT2D)
                .parseParam()
                .named(
                        "xmin",
                        "xmax",

                        "majorGridSpacing",
                        "showMajorGrid",
                        "majorGridColor",
                        "majorGridStroke",

                        "minorGridSpacing",
                        "showMinorGrid",
                        "minorGridColor",
                        "minorGridStroke"
                ).end()
                .processChildren(this::processChildren)
                .renderComponent(this::renderMain)
        ;
    }

    public void processChildren(NTxAllArgumentReader info, NTxNodeCustomBuilderContext buildContext) {
        List<FunctionPlotInfo> all = new FunctionPlotInfoLoader().loadBody(info.element(), buildContext);
        info.node().setUserObject("def", all);
    }





    public void renderMain(NTxNode p, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext builderContext) {
        NTxBounds2 selfBounds = renderContext.selfBounds(p, null, null);
        double minX = NTxValue.of(renderContext.engine().evalExpression(p.getPropertyValue("xmin").orElse(NElement.ofDouble(-100)), p, renderContext.varProvider())).asDouble().orElse(-100.0);
        double maxX = NTxValue.of(renderContext.engine().evalExpression(p.getPropertyValue("xmax").orElse(NElement.ofDouble(+100)), p, renderContext.varProvider())).asDouble().orElse(+100.0);
        double minY = -100;
        double maxY = 100;
        boolean zoom = true;
        NTxMinMax minMaxY = new NTxMinMax();

        Paint color = renderContext.getForegroundColor(p, true);

        NTxGraphics g = renderContext.graphics();
        if (!renderContext.isDry()) {
            java.util.List<FunctionPlotInfo> plotDefinitions = (List<FunctionPlotInfo>) p.getUserObject("def").orNull();

            g.setPaint(color);

            NTxBounds2 bounds = renderContext.getBounds();
            int steps = (int) (bounds.getHeight() * 2);

            List<Plot2DData> allData = new ArrayList<>();
            for (FunctionPlotInfo pld : plotDefinitions) {
                double[] xx = ArrayUtils.dtimes(minX, maxX, steps);
                NDoubleFunction ff = MyPlotNExprEvaluator.compileFunctionX(pld, renderContext);
                if (ff != null) {
                    Plot2DData pd = new Plot2DData(ff, xx, minMaxY);
                    if (color instanceof java.awt.Color) {
                        pd.color = (Color) color;
                    }
                    if (pld.color != null) {
                        NElement ev = builderContext.engine().evalExpression(pld.color, p, renderContext.varProvider());
                        pd.color = NTxValue.of(ev).asColor().orElse(pd.color);
                    }
                    if (pld.stroke != null) {
                        NElement ev = builderContext.engine().evalExpression(pld.color, p, renderContext.varProvider());
                        if (ev != null && !ev.isNull()) {
                            Stroke stroke = renderContext.graphics().createStroke(ev);
                            if (stroke != null) {
                                pd.stroke = stroke;
                            }
                        }
                    }
                    allData.add(pd);
                }
            }

            DrawContext drawContext = new DrawContext(bounds, minX, maxX, minY, maxY, zoom, minMaxY);

            drawAxises(drawContext, g, p, renderContext, builderContext);
            for (Plot2DData pd : allData) {
                drawFunction(pd, drawContext, g, p, renderContext, builderContext);
            }
            renderContext.paintDebugBox(p, selfBounds);
            renderContext.paintBorderLine(p, selfBounds);
        }
    }

    private void drawFunction(Plot2DData pd, DrawContext drawContext, NTxGraphics g, NTxNode p, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext builderContext) {
        //draw function
        Stroke ostroke = g.getStroke();
        g.setColor(pd.color);
        g.setStroke(pd.stroke);
        double[] xx = pd.xx;
        double[] yy = pd.yy;
        boolean animate = renderContext.isAnimate();
        long pageStartTime = renderContext.getPageStartTime();
        long now = System.currentTimeMillis();
        long max=500;
        double td = 1;
        if(animate && pageStartTime>0){
             long t=now-pageStartTime;
             if(t<=0) {
                 t = max;
             }else if(t>=max){
                 t=max;
             }
            td=t/(double)max;
        }

        for (int i = 1; i < xx.length; i++) {
            double yy1 = yy[i];
            if (drawContext.acceptY(yy1)) {
                double yy0 = yy[i - 1];
                if (drawContext.acceptY(yy0)) {
                    if(animate){
                        yy1=yy1*td;
                        yy0=yy0*td;
                    }
                    int fromX = (int) drawContext.xPixels(xx[i - 1]);
                    int fromY = (int) drawContext.yPixels(yy0);
                    int toX = (int) drawContext.xPixels(xx[i]);
                    int toY = (int) drawContext.yPixels(yy1);
                    g.drawLine(fromX, fromY, toX, toY);
                }
            }
        }
        g.setStroke(ostroke);
    }

    private void drawAxises(DrawContext drawContext, NTxGraphics g, NTxNode p, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext builderContext) {
        Stroke mainStroke = new BasicStroke(1.0f);
        Stroke stepStroke = new BasicStroke(1.0f, // Line width of 2 pixels
                BasicStroke.CAP_BUTT, // No added decoration at line ends
                BasicStroke.JOIN_MITER, // How segments join
                10.0f, // Miter limit
                new float[]{10.0f, 10.0f}, // The dash pattern array
                0.0f);
        Stroke subStepStroke = new BasicStroke(1.0f, // Line width of 2 pixels
                BasicStroke.CAP_BUTT, // No added decoration at line ends
                BasicStroke.JOIN_MITER, // How segments join
                10.0f, // Miter limit
                new float[]{5.0f, 5.0f}, // The dash pattern array
                0.0f);
        Color mainColor = Color.gray;
        Color stepColor = Color.lightGray;
        Color subStepColor = Color.lightGray;

        double epsilon = 1E-10;
        if (drawContext.gridSubStepX >= 0 && drawContext.gridSubStepX != drawContext.gridStepX) {
            g.setColor(subStepColor);
            Stroke ostroke = g.getStroke();
            g.setStroke(subStepStroke);
            for (double xi : ArrayUtils.findMultiplesFastDouble(drawContext.gridMinX, drawContext.gridMaxX, drawContext.gridSubStepX, epsilon)) {
                int pxi = (int) drawContext.xPixels(xi);
                g.drawLine(pxi, drawContext.componentMinY, pxi, drawContext.componentMinY + drawContext.componentHeight);
            }
            g.setStroke(ostroke);
        }

        if (drawContext.gridSubStepY >= 0 && drawContext.gridSubStepY != drawContext.gridStepY) {
            g.setColor(subStepColor);
            Stroke ostroke = g.getStroke();
            g.setStroke(subStepStroke);
            for (double yi : ArrayUtils.findMultiplesFastDouble(drawContext.gridMinY, drawContext.gridMaxY, drawContext.gridSubStepY, epsilon)) {
                int pyi = (int) drawContext.yPixels(yi);
                g.drawLine(drawContext.componentMinX, pyi, drawContext.componentMinX + drawContext.componentWidth, pyi);
            }
            g.setStroke(ostroke);
        }

        if (drawContext.gridStepX >= 0) {
            g.setColor(stepColor);
            Stroke ostroke = g.getStroke();
            g.setStroke(stepStroke);
            for (double xi : ArrayUtils.findMultiplesFastDouble(drawContext.gridMinX, drawContext.gridMaxX, drawContext.gridStepX, epsilon)) {
                int pxi = (int) drawContext.xPixels(xi);
                if (Math.abs(xi) <= epsilon) {
                    g.setColor(mainColor);
                    g.setStroke(mainStroke);
                    g.drawLine(pxi, drawContext.componentMinY, pxi, drawContext.componentMinY + drawContext.componentHeight);
                    g.drawArrayHead(new NTxPoint2D(0, 0), new NTxVector2D(0, 1), new NTxArrow(NTxArrowType.TRIANGLE_FULL));
                    g.setStroke(stepStroke);
                    g.setColor(stepColor);
                } else {
                    g.drawLine(pxi, drawContext.componentMinY, pxi, drawContext.componentMinY + drawContext.componentHeight);
                }
            }
            g.setStroke(ostroke);
        }

        if (drawContext.gridStepY >= 0) {
            g.setColor(stepColor);
            Stroke ostroke = g.getStroke();
            g.setStroke(stepStroke);
            for (double yi : ArrayUtils.findMultiplesFastDouble(drawContext.gridMinY, drawContext.gridMaxY, drawContext.gridStepY, epsilon)) {
                int pyi = (int) drawContext.yPixels(yi);
                if (Math.abs(yi) <= epsilon) {
                    g.setColor(mainColor);
                    g.setStroke(mainStroke);
                    g.drawLine(drawContext.componentMinX, pyi, drawContext.componentMinX + drawContext.componentWidth, pyi);
                    g.drawArrayHead(new NTxPoint2D(0, 0), new NTxVector2D(1, 0), new NTxArrow(NTxArrowType.TRIANGLE_FULL));
                    g.setStroke(stepStroke);
                    g.setColor(stepColor);
                } else {
                    g.drawLine(drawContext.componentMinX, pyi, drawContext.componentMinX + drawContext.componentWidth, pyi);
                }
            }
            g.setStroke(ostroke);
        }
    }

}

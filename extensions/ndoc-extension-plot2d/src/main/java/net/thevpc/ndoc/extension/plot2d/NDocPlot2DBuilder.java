package net.thevpc.ndoc.extension.plot2d;

import net.thevpc.ndoc.api.document.NDocArrow;
import net.thevpc.ndoc.api.document.NDocArrowType;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.elem2d.Vector2D;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.parser.NDocAllArgumentReader;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.MinMax;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.expr.*;
import net.thevpc.nuts.util.NDoubleFunction;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class NDocPlot2DBuilder implements NDocNodeBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.PLOT2D)
                .parseParam()
                .named(
                        NDocPropName.WIDTH,
                        NDocPropName.HEIGHT,
                        "xmin",
                        "xmax",
                        "function"
                ).end()
                .processChildren(this::processChildren)
                .renderComponent(this::renderMain)
        ;
    }

    public static class FunctionPlotInfo {
        NElement fexpr;
        String var1;
        String var2;
        String var3;
        String var4;
        int args;
        NElement title;
        NElement color;
        NElement stroke;
        NDoubleFunction f;
    }

    public void processChildren(NDocAllArgumentReader info, NDocNodeCustomBuilderContext buildContext) {
        NListContainerElement nListContainerElement = info.element().asListContainer().orNull();
        java.util.List<FunctionPlotInfo> all = new ArrayList<>();
        if (nListContainerElement != null && !nListContainerElement.isAnyUplet()) {
            List<NElement> c = nListContainerElement.children();
            boolean somePairs = c.stream().anyMatch(x -> x.isPair());
            boolean someObjects = c.stream().anyMatch(x -> x.isObject());
            if (somePairs) {
                all.add(load(nListContainerElement.asObject().get(), buildContext));
            } else if (someObjects) {
                for (NElement child : c) {
                    if (child.isObject()) {
                        FunctionPlotInfo a = load(child.asObject().get(), buildContext);
                        if (a != null) {
                            all.add(a);
                        }
                    }
                }
            }
        }
        if (all.isEmpty()) {
            FunctionPlotInfo f = new FunctionPlotInfo();
            all.add(f);
        }
        info.node().setUserObject("def", all);
    }

    private FunctionPlotInfo load(NObjectElement child, NDocNodeCustomBuilderContext buildContext) {
        FunctionPlotInfo i = new FunctionPlotInfo();
        for (NElement e : child.children()) {
            if (e.isNamedPair()) {
                switch (e.asPair().get().key().asStringValue().get()) {
                    case "title": {
                        i.title = e.asPair().get().value();
                        break;
                    }
                    case "color": {
                        i.color = e.asPair().get().value();
                        break;
                    }
                    case "stroke": {
                        i.stroke = e.asPair().get().value();
                        break;
                    }
                    default: {
                        buildContext.engine().log().log(NMsg.ofC("unexpected function declaration %s", NDocUtils.snippet(e)));
                    }
                }
            } else if (e.isPair()) {
                NPairElement p = e.asPair().get();
                NElement k = p.key();
                if (k.isNamedUplet("f")) {
                    NUpletElement fk = k.asUplet().get();
                    if (fk.params().size() == 1 && fk.params().get(0).isName()) {
                        i.fexpr = p.value();
                        i.var1 = fk.params().get(0).asNameValue().get();
                        i.args = 1;
                    } else if (fk.params().size() == 2 && fk.params().get(0).isName() && fk.params().get(1).isName()) {
                        i.fexpr = p.value();
                        i.var1 = fk.params().get(0).asNameValue().get();
                        i.var2 = fk.params().get(1).asNameValue().get();
                        i.args = 2;
                    } else if (fk.params().size() == 3 && fk.params().get(0).isName() && fk.params().get(1).isName() && fk.params().get(2).isName()) {
                        i.fexpr = p.value();
                        i.var1 = fk.params().get(0).asNameValue().get();
                        i.var2 = fk.params().get(1).asNameValue().get();
                        i.var3 = fk.params().get(2).asNameValue().get();
                        i.args = 3;
                    } else if (fk.params().size() == 4 && fk.params().get(0).isName() && fk.params().get(1).isName() && fk.params().get(2).isName() && fk.params().get(3).isName()) {
                        i.fexpr = p.value();
                        i.var1 = fk.params().get(0).asNameValue().get();
                        i.var2 = fk.params().get(1).asNameValue().get();
                        i.var3 = fk.params().get(2).asNameValue().get();
                        i.var4 = fk.params().get(3).asNameValue().get();
                        i.args = 4;
                    } else {
                        buildContext.engine().log().log(NMsg.ofC("unexpected function declaration %s", NDocUtils.snippet(k)));
                    }
                } else {
                    buildContext.engine().log().log(NMsg.ofC("unexpected function declaration %s", NDocUtils.snippet(k)));
                }
            }
        }
        return i;
    }

    NDoubleFunction compileFunctionX(FunctionPlotInfo e, NDocNodeRendererContext renderContext) {
        NExprMutableDeclarations d = NDocExprHelper.create();
        NOptional<NExprNode> ne = d.parse(e.fexpr.isAnyString() ? e.fexpr.asStringValue().get() : NDocUtils.removeCompilerDeclarationPathAnnotations(e.fexpr).toString(true));
        if (!ne.isPresent()) {
            renderContext.engine().log().log(NMsg.ofC("unable to parse expression %s : %s", ne.getMessage(), e.fexpr));
            return null;
        }
        NExprNode nExprNode = ne.get();
        return x -> {
            NOptional<Object> r = nExprNode.eval(
                    d.newDeclarations(new MyPlotNExprEvaluator(e, d, x, 0, 0, 0))
            );
            return NDocExprHelper.asDouble(r.orNull());
        };
    }

    public void renderMain(NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext builderContext) {
        NDocBounds2 selfBounds = renderContext.selfBounds(p, null, null);
        double minX = NDocValue.of(renderContext.engine().evalExpression(p.getPropertyValue("xmin").orElse(NElement.ofDouble(-100)), p, renderContext.varProvider())).asDouble().orElse(-100.0);
        double maxX = NDocValue.of(renderContext.engine().evalExpression(p.getPropertyValue("xmax").orElse(NElement.ofDouble(+100)), p, renderContext.varProvider())).asDouble().orElse(+100.0);
        double minY = -100;
        double maxY = 100;
        boolean zoom = true;
        MinMax minMaxY = new MinMax();

        Paint color = renderContext.getForegroundColor(p, true);

        NDocGraphics g = renderContext.graphics();
        if (!renderContext.isDry()) {
            java.util.List<FunctionPlotInfo> plotDefinitions = (List<FunctionPlotInfo>) p.getUserObject("def").orNull();

            g.setPaint(color);

            NDocBounds2 bounds = renderContext.getBounds();
            int steps = (int) (bounds.getHeight() * 2);

            List<Plot2DData> allData = new ArrayList<>();
            for (FunctionPlotInfo pld : plotDefinitions) {
                double[] xx = ArrayUtils.dtimes(minX, maxX, steps);
                NDoubleFunction ff = compileFunctionX(pld, renderContext);
                if(ff!=null) {
                    Plot2DData pd = new Plot2DData(ff, xx, minMaxY);
                    if(color instanceof java.awt.Color) {
                        pd.color=(Color) color;
                    }
                    if(pld.color!=null){
                        NElement ev = builderContext.engine().evalExpression(pld.color, p, renderContext.varProvider());
                        pd.color=NDocValue.of(ev).asColor().orElse(pd.color);
                    }
                    if(pld.stroke!=null){
                        NElement ev = builderContext.engine().evalExpression(pld.color, p, renderContext.varProvider());
                        if(ev!=null && !ev.isNull()){
                            Stroke stroke = renderContext.graphics().createStroke(ev);
                            if(stroke!=null){
                                pd.stroke=stroke;
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

    private void drawFunction(Plot2DData pd, DrawContext drawContext, NDocGraphics g, NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext builderContext) {
        //draw function
        Stroke ostroke = g.getStroke();
        g.setColor(pd.color);
        g.setStroke(pd.stroke);
        double[] xx = pd.xx;
        double[] yy = pd.yy;
        for (int i = 1; i < xx.length; i++) {
            if (drawContext.acceptY(yy[i])) {
                if (drawContext.acceptY(yy[i - 1])) {
                    int fromX = (int) drawContext.xPixels(xx[i - 1]);
                    int fromY = (int) drawContext.yPixels(yy[i - 1]);
                    int toX = (int) drawContext.xPixels(xx[i]);
                    int toY = (int) drawContext.yPixels(yy[i]);
                    g.drawLine(fromX, fromY, toX, toY);
                }
            }
        }
        g.setStroke(ostroke);
    }

    private void drawAxises(DrawContext drawContext, NDocGraphics g, NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext builderContext) {
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
            for (double xi : findMultiplesFastDouble(drawContext.gridMinX, drawContext.gridMaxX, drawContext.gridSubStepX, epsilon)) {
                int pxi = (int) drawContext.xPixels(xi);
                g.drawLine(pxi, drawContext.componentMinY, pxi, drawContext.componentMinY + drawContext.componentHeight);
            }
            g.setStroke(ostroke);
        }

        if (drawContext.gridSubStepY >= 0 && drawContext.gridSubStepY != drawContext.gridStepY) {
            g.setColor(subStepColor);
            Stroke ostroke = g.getStroke();
            g.setStroke(subStepStroke);
            for (double yi : findMultiplesFastDouble(drawContext.gridMinY, drawContext.gridMaxY, drawContext.gridSubStepY, epsilon)) {
                int pyi = (int) drawContext.yPixels(yi);
                g.drawLine(drawContext.componentMinX, pyi, drawContext.componentMinX + drawContext.componentWidth, pyi);
            }
            g.setStroke(ostroke);
        }

        if (drawContext.gridStepX >= 0) {
            g.setColor(stepColor);
            Stroke ostroke = g.getStroke();
            g.setStroke(stepStroke);
            for (double xi : findMultiplesFastDouble(drawContext.gridMinX, drawContext.gridMaxX, drawContext.gridStepX, epsilon)) {
                int pxi = (int) drawContext.xPixels(xi);
                if (Math.abs(xi) <= epsilon) {
                    g.setColor(mainColor);
                    g.setStroke(mainStroke);
                    g.drawLine(pxi, drawContext.componentMinY, pxi, drawContext.componentMinY + drawContext.componentHeight);
                    g.drawArrayHead(new NDocPoint2D(0, 0), new Vector2D(0, 1), new NDocArrow(NDocArrowType.TRIANGLE_FULL));
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
            for (double yi : findMultiplesFastDouble(drawContext.gridMinY, drawContext.gridMaxY, drawContext.gridStepY, epsilon)) {
                int pyi = (int) drawContext.yPixels(yi);
                if (Math.abs(yi) <= epsilon) {
                    g.setColor(mainColor);
                    g.setStroke(mainStroke);
                    g.drawLine(drawContext.componentMinX, pyi, drawContext.componentMinX + drawContext.componentWidth, pyi);
                    g.drawArrayHead(new NDocPoint2D(0, 0), new Vector2D(1, 0), new NDocArrow(NDocArrowType.TRIANGLE_FULL));
                    g.setStroke(stepStroke);
                    g.setColor(stepColor);
                } else {
                    g.drawLine(drawContext.componentMinX, pyi, drawContext.componentMinX + drawContext.componentWidth, pyi);
                }
            }
            g.setStroke(ostroke);
        }
    }

    public static double[] findMultiplesFastDouble(double xmin, double xmax, double step, double epsilon) {
        if (step <= epsilon) { // Check if step is effectively zero or negative
            throw new IllegalArgumentException("Step must be a positive number.");
        }
        if (xmin > xmax + epsilon) { // Account for xmin slightly greater than xmax due to precision
            return new double[0]; // No multiples if xmin is greater than xmax
        }

        java.util.List<Double> multiples = new ArrayList<>();

        double currentMultiple;

        double numSteps = xmin / step;

        long floorSteps = (long) Math.floor(numSteps);
        currentMultiple = floorSteps * step;

        if (currentMultiple < xmin - epsilon) {
            currentMultiple += step;
        }

        if (Math.abs(xmin - currentMultiple) < epsilon) {
            currentMultiple = xmin; // Start exactly at xmin if it's an approximate multiple
        }

        if (currentMultiple > xmax + epsilon) {
            return new double[0];
        }

        while (currentMultiple < xmax + epsilon) {
            multiples.add(currentMultiple);
            currentMultiple += step;
        }

        return multiples.stream().mapToDouble(Double::doubleValue).toArray();
    }

}

package net.thevpc.ntexup.extension.plot2d;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxMinMax;
import net.thevpc.ntexup.api.util.NTxNumberUtils;
import net.thevpc.ntexup.extension.plot2d.expr.NTxPlotNExprEvaluator;
import net.thevpc.ntexup.extension.plot2d.model.NTxDrawContext;
import net.thevpc.ntexup.extension.plot2d.model.NTxFunctionPlotInfo;
import net.thevpc.ntexup.extension.plot2d.model.NTxPlot2DData;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.time.NChronometer;
import net.thevpc.nuts.util.NDoubleFunction;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;
import java.util.List;

class NTxDrawContextRenderCompiler {
    public static NTxDrawContext compile(NTxNode p, NTxNodeRendererContext renderContext){
        double[] xValues = NTxValue.of(renderContext.engine().evalExpression(p.getPropertyValue("x").orElse(NElement.ofDoubleArray(NTxNumberUtils.dsteps(100,-100,1))), p, renderContext.varProvider())).asDoubleArray().orElse(NTxNumberUtils.dsteps(100,-100,1));
        double minY = -100;
        double maxY = 100;
        boolean zoom = true;
        NTxMinMax minMaxY = new NTxMinMax();

        Paint color = renderContext.getForegroundColor(p, true);

        NTxBounds2 bounds = renderContext.getBounds();
        NTxDrawContext drawContext = new NTxDrawContext(bounds, xValues, minY, maxY, zoom, minMaxY);
        java.util.List<NTxFunctionPlotInfo> plotDefinitions = (List<NTxFunctionPlotInfo>) p.getUserObject("def").orNull();

//        int steps = (int) (bounds.getHeight() * 2);

        for (NTxFunctionPlotInfo pld : plotDefinitions) {
            NTxPlot2DData pd = new NTxPlot2DData(pld);
            if (color instanceof java.awt.Color) {
                pd.color = (Color) color;
            }
            if (pld.color != null) {
                NElement ev = renderContext.engine().evalExpression(pld.color, p, renderContext.varProvider());
                pd.color = NTxValue.of(ev).asColor().orElse(pd.color);
            }
            if (pld.title != null) {
                NElement ev = renderContext.engine().evalExpression(pld.title, p, renderContext.varProvider());
                pd.title = NTxValue.of(ev).asString().orNull();
            }
            if (pld.stroke != null) {
                NElement ev = renderContext.engine().evalExpression(pld.color, p, renderContext.varProvider());
                if (ev != null && !ev.isNull()) {
                    Stroke stroke = renderContext.graphics().createStroke(ev);
                    if (stroke != null) {
                        pd.stroke = stroke;
                    }
                }
            }

            switch (pd.pld.source){
                case FUNCTION_X:{
                    NDoubleFunction ff = NTxPlotNExprEvaluator.compileFunctionX(pld, renderContext);
                    if (ff != null) {
                        NChronometer c = NChronometer.startNow();
                        pd.prepareX(ff, xValues, minMaxY);
                        c.stop();
                        renderContext.engine().log().log(NMsg.ofC("FUNCTION_X : %s",c));
                        drawContext.allData.add(pd);
                    }
                    break;
                }
            }
        }
        drawContext.build();
        return drawContext;
    }
}

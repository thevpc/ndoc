package net.thevpc.ntexup.extension.plot2d.jfc;

import net.thevpc.ntexup.extension.plot2d.model.NTxDrawContext;
import net.thevpc.ntexup.extension.plot2d.model.NTxPlot2DData;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;

public class GradientXYLineAndShapeRenderer extends XYLineAndShapeRenderer {
    private final XYSeriesCollection dataset;
    private final NTxDrawContext drawContext;

    public GradientXYLineAndShapeRenderer(boolean lines, boolean shapes, XYSeriesCollection dataset, NTxDrawContext drawContext) {
        super(lines, shapes);
        this.dataset = dataset;
        this.drawContext = drawContext;
    }

    @Override
    public Paint getItemPaint(int series, int item) {
        double y = dataset.getYValue(series, item);
        double ratio = (y - drawContext.gridMinY) / (drawContext.gridHeight);
        return new Color((float) ratio, 0f, 1f - (float) ratio);
    }
}

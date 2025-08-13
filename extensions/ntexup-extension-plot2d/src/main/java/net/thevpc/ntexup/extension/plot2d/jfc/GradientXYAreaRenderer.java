package net.thevpc.ntexup.extension.plot2d.jfc;


import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class GradientXYAreaRenderer extends XYAreaRenderer {

    private final double minY;
    private final double maxY;

    public GradientXYAreaRenderer(double minY, double maxY) {
        super();
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        if (item == 0) return; // need at least two points

        double x1 = dataset.getXValue(series, item - 1);
        double y1 = dataset.getYValue(series, item - 1);
        double x2 = dataset.getXValue(series, item);
        double y2 = dataset.getYValue(series, item);

        double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
        double transX2 = domainAxis.valueToJava2D(x2, dataArea, plot.getDomainAxisEdge());
        double transY2 = rangeAxis.valueToJava2D(y2, dataArea, plot.getRangeAxisEdge());

        double baseline = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());

        // Build a trapezoid under this segment
        Path2D area = new Path2D.Double();
        area.moveTo(transX1, baseline);
        area.lineTo(transX1, transY1);
        area.lineTo(transX2, transY2);
        area.lineTo(transX2, baseline);
        area.closePath();

        // Choose color based on average height
        double avgY = (y1 + y2) / 2.0;
        double ratio = (avgY - minY) / (maxY - minY);
        ratio = Math.max(0, Math.min(1, ratio));
        Color color = blend(Color.BLUE, Color.RED, ratio);

        g2.setPaint(color);
        g2.fill(area);
        g2.setPaint(Color.BLACK); // optional outline
        g2.draw(area);
    }

    private static Color blend(Color c1, Color c2, double ratio) {
        int r = (int) (c1.getRed() + ratio * (c2.getRed() - c1.getRed()));
        int g = (int) (c1.getGreen() + ratio * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + ratio * (c2.getBlue() - c1.getBlue()));
        return new Color(r, g, b);
    }
}

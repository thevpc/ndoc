package net.thevpc.ntexup.extension.plot2d;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.util.NTxMinMax;

class DrawContext {

    double gridMinX;
    double gridMaxX;

    double gridMinY;
    double gridMaxY;

    double gridStepX = 10;
    double gridStepY = 10;

    double gridSubStepX = 1;
    double gridSubStepY = 1;

    double gridWidth;
    double gridHeight;
    double componentWidth;
    double componentHeight;
    double componentMinX;
    double componentMinY;

    public DrawContext(NTxBounds2 bounds, double minX, double maxX, double minY, double maxY, boolean zoomY, NTxMinMax minMaxY) {
        this.componentWidth = bounds.getWidth();
        this.componentHeight = bounds.getHeight();
        this.componentMinX = bounds.getX();
        this.componentMinY = bounds.getY();

        double yWidth = 0;
        if (zoomY && Double.isFinite(minMaxY.getMax()) && Double.isFinite(minMaxY.getMin())) {
            yWidth = minMaxY.getMax() - minMaxY.getMin();
            minY = minMaxY.getMin() - yWidth * 0.1;
            maxY = minMaxY.getMax() + yWidth * 0.1;
        } else {
            minY = -2.5;
            maxY = +2.5;
        }
        gridMinX = minX;
        gridMaxX = maxX;
        gridMinY = minY;
        gridMaxY = maxY;
        this.gridWidth = gridMaxX - gridMinX;
        this.gridHeight = gridMaxY - gridMinY;
    }

    public double xPixels(double a) {
        return (a - gridMinX) * componentWidth / gridWidth + componentMinX;
    }

    public double yPixels(double a) {
        return (gridHeight - (a - gridMinY)) * componentHeight / gridHeight + componentMinY;
    }

    public double wPixels(double a) {
        return a * componentWidth / gridWidth;
    }

    public double hPixels(double a) {
        return a * componentHeight / gridHeight;
    }

    boolean acceptY(double y) {
        return !Double.isNaN(y)
                && Double.isFinite(y)
                && y >= gridMinY
                && y <= gridMaxY;
    }

    boolean acceptX(double x) {
        return !Double.isNaN(x)
                && Double.isFinite(x)
                && x >= gridMinX
                && x <= gridMaxX;
    }

}

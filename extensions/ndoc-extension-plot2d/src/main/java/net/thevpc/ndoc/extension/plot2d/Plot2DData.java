package net.thevpc.ndoc.extension.plot2d;

import net.thevpc.ndoc.api.util.MinMax;
import net.thevpc.nuts.util.NDoubleFunction;

import java.awt.*;

class Plot2DData {
    double[] xx;
    double[] yy;
    String title;
    Color color = Color.black;
    Stroke stroke = new BasicStroke(2.0f);

    public Plot2DData(NDoubleFunction f, double[] xx, MinMax minMaxY) {
        this.xx = xx;
        this.yy = new double[xx.length];
        for (int i = 0; i < xx.length; i++) {
            yy[i] = f.apply(xx[i]);
            if (Double.isFinite(yy[i])) {
                minMaxY.registerValue(yy[i]);
            }
        }
    }
}

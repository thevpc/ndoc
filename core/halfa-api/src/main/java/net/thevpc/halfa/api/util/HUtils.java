package net.thevpc.halfa.api.util;

import net.thevpc.halfa.api.model.elem3d.HPoint3D;

public class HUtils {
    public static final MinMax minMaxZ(HPoint3D[] points) {
        MinMax m = new MinMax();
        for (HPoint3D point : points) {
            m.registerValue(point.z);
        }
        return m;
    }

    public static final double[] dtimes(double min, double max, int times) {
        double[] d = new double[times];
        if (times == 1) {
            d[0] = min;
        } else {
            double step = (max - min) / (times - 1);
            for (int i = 0; i < d.length; i++) {
                d[i] = min + i * step;
            }
        }
        return d;
    }
}

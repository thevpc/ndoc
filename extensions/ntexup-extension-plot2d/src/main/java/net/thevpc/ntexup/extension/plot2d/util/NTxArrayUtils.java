package net.thevpc.ntexup.extension.plot2d.util;

import java.util.ArrayList;

public class NTxArrayUtils {
    public static double[] dtimes(double min, double max, int times) {
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

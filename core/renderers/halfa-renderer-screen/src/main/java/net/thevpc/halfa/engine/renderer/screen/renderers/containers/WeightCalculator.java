package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeightCalculator {
    private double all;
    private List<W> children = new ArrayList<>();

    public static void main(String[] args) {
        WeightCalculator wc = new WeightCalculator(400);
        wc.add(1, 0, -1);
        wc.add(2, 0, -1);
        wc.add(1, 0, 5);
        System.out.println(Arrays.toString(wc.calculate()));
    }

    public WeightCalculator(double all) {
        this.all = all;
    }

    private static class W {
        double weight;
        double min;
        double max;
        double width;

        public W(double weight, double min, double max, double width) {
            this.weight = weight;
            this.min = min;
            this.max = max;
            this.width = width;
        }
    }

    public void add(double factor, double min, double max) {
        children.add(new W(factor, min, max, 0));
    }

    public double[] calculate() {
        int count = children.size();
        double[] maxAlpha = new double[count];
        double weightSum = 0;
        double maxAlphaSum = 0;
        double[] weights = new double[count];
        double[] mins = new double[count];
        for (int i = 0; i < count; i++) {
            weights[i] = children.get(i).weight;
            mins[i] = children.get(i).min;
        }
        for (int i = 0; i < count; i++) {
            weightSum += weights[i];
        }
        for (int i = 0; i < count; i++) {
            maxAlpha[i] = mins[i] * weightSum;
            maxAlphaSum += maxAlpha[i];
        }
        if (maxAlphaSum == 0) {
            double[] ret = new double[count];
            for (int i = 0; i < count; i++) {
                ret[i] = weights[i] * all/weightSum;
            }
            return ret;
        }
        for (int i = 0; i < count; i++) {
            maxAlpha[i] = maxAlpha[i] / maxAlphaSum;
        }
        double[] ret = new double[count];
        for (int i = 0; i < count; i++) {
            ret[i] = maxAlpha[i] * all;
        }
        return ret;
    }
}

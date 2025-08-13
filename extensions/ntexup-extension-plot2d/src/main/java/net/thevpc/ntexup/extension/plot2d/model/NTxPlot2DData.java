package net.thevpc.ntexup.extension.plot2d.model;

import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxMinMax;
import net.thevpc.nuts.util.NDoubleFunction;

import java.awt.*;

public class NTxPlot2DData {
    public double[] xx;
    public double[] yy;
    public String title;
    public Color color = Color.black;
    public Stroke stroke = new BasicStroke(2.0f);
    public NTxFunctionPlotInfo pld;
    public boolean lineShapes=false;

    public NTxPlot2DData(NTxFunctionPlotInfo pld) {
        this.pld = pld;
    }

    public void prepareX(NDoubleFunction f, double[] xx, NTxMinMax minMaxY) {
        this.xx = xx;
        this.yy = new double[xx.length];
        for (int i = 0; i < xx.length; i++) {
            yy[i] = f.apply(xx[i]);
            if (Double.isFinite(yy[i])) {
                minMaxY.registerValue(yy[i]);
            }
        }
    }

    public double[] animatedYY(NTxNodeRendererContext renderContext) {
        double[] xx = this.xx;
        double[] yy = this.yy;
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
        double[] yy2 = new double[yy.length];
        for (int i = 0; i < yy.length; i++) {
            yy2[i] = yy[i]*td;
        }
        return yy2;
    }

}

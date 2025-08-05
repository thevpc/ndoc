package net.thevpc.ndoc.extension.plot2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NDoubleFunction;

public class FunctionPlotInfo {
    PlotType plotType;
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

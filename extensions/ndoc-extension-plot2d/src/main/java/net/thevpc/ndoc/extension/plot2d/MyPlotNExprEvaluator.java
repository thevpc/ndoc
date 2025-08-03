package net.thevpc.ndoc.extension.plot2d;

import net.thevpc.nuts.expr.*;
import net.thevpc.nuts.util.NOptional;

import java.util.HashMap;
import java.util.Map;

class MyPlotNExprEvaluator implements NExprEvaluator {
    private final NDocPlot2DBuilder.FunctionPlotInfo e;
    private NExprMutableDeclarations d;
    private Map<String, NExprVar> extraVars = new HashMap<>();

    public MyPlotNExprEvaluator(NDocPlot2DBuilder.FunctionPlotInfo e, NExprMutableDeclarations d, double x, double y, double z, double t) {
        this.e = e;
        this.d = d;
        addVar(e.var1, x);
        if (e.args >= 2) {
            addVar(e.var2, y);
            if (e.args >= 3) {
                addVar(e.var3, z);
                if (e.args >= 4) {
                    addVar(e.var4, t);
                }
            }
        }
    }

    private void addVar(String varName, Object varValue) {
        extraVars.put(varName, d.ofVar(varName, varValue));
    }

    @Override
    public NOptional<NExprVar> getVar(String varName, NExprDeclarations context) {
        return NOptional.ofNamed(extraVars.get(varName), "var " + varName);
    }
}

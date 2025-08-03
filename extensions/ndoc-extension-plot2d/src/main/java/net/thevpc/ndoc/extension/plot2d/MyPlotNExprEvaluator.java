package net.thevpc.ndoc.extension.plot2d;

import net.thevpc.nuts.expr.*;
import net.thevpc.nuts.util.NOptional;

class MyPlotNExprEvaluator implements NExprEvaluator {
    private final NDocPlot2DBuilder.FunctionPlotInfo e;
    private NExprVar xvar;
    private NExprVar yvar;
    private NExprVar zvar;
    private NExprVar tvar;

    public MyPlotNExprEvaluator(NDocPlot2DBuilder.FunctionPlotInfo e, NExprMutableDeclarations d, double x, double y, double z, double t) {
        this.e = e;
        this.xvar = d.ofVar(e.var1, x);
        if (e.args >= 2) {
            this.yvar = d.ofVar(e.var2, y);
            if (e.args >= 3) {
                this.zvar = d.ofVar(e.var3, z);
                if (e.args >= 4) {
                    this.tvar = d.ofVar(e.var4, t);
                }
            }
        }
    }

    @Override
    public NOptional<NExprFct> getFunction(String fctName, NExprNodeValue[] args, NExprDeclarations context) {
        return NExprEvaluator.super.getFunction(fctName, args, context);
    }

    @Override
    public NOptional<NExprConstruct> getConstruct(String constructName, NExprNodeValue[] args, NExprDeclarations context) {
        return NExprEvaluator.super.getConstruct(constructName, args, context);
    }

    @Override
    public NOptional<NExprOp> getOperator(String opName, NExprOpType type, NExprNodeValue[] args, NExprDeclarations context) {
        return NExprEvaluator.super.getOperator(opName, type, args, context);
    }

    @Override
    public NOptional<NExprVar> getVar(String varName, NExprDeclarations context) {
        if (e.var1.equals(varName)) {
            return NOptional.of(xvar);
        }
        if (e.args >= 2) {
            if (e.var2.equals(varName)) {
                return NOptional.of(yvar);
            }
            if (e.args >= 3) {
                if (e.var3.equals(varName)) {
                    return NOptional.of(zvar);
                }
                if (e.args >= 4) {
                    if (e.var4.equals(varName)) {
                        return NOptional.of(tvar);
                    }
                }
            }
        }
        return NExprEvaluator.super.getVar(varName, context);
    }
}

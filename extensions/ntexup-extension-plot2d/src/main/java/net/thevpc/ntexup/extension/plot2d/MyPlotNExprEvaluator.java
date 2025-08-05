package net.thevpc.ntexup.extension.plot2d;

import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.nuts.expr.*;
import net.thevpc.nuts.util.NDoubleFunction;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.util.HashMap;
import java.util.Map;

class MyPlotNExprEvaluator implements NExprEvaluator {
    private final FunctionPlotInfo e;
    private NExprMutableDeclarations d;
    private Map<String, NExprVar> extraVars = new HashMap<>();


    static NDoubleFunction compileFunctionX(FunctionPlotInfo e, NTxNodeRendererContext renderContext) {
        NExprMutableDeclarations d = NTxExprHelper.create();
        NOptional<NExprNode> ne = d.parse(e.fexpr.isAnyString() ? e.fexpr.asStringValue().get() : NTxUtils.removeCompilerDeclarationPathAnnotations(e.fexpr).toString(true));
        if (!ne.isPresent()) {
            renderContext.engine().log().log(NMsg.ofC("unable to parse expression %s : %s", ne.getMessage(), e.fexpr));
            return null;
        }
        NExprNode nExprNode = ne.get();
        return x -> {
            NOptional<Object> r = nExprNode.eval(
                    d.newDeclarations(new MyPlotNExprEvaluator(e, d, x, 0, 0, 0))
            );
            return NTxExprHelper.asDouble(r.orNull());
        };
    }

    public MyPlotNExprEvaluator(FunctionPlotInfo e, NExprMutableDeclarations d, double x, double y, double z, double t) {
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

package net.thevpc.ntexup.extension.plot2d.expr;

import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.nuts.expr.*;

public class NTxExprHelper {
    public static NExprMutableDeclarations create(){
        NExprMutableDeclarations d = NExprs.of().newMutableDeclarations();
        d.declareFunction("sin", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.sin(asDouble(a.eval(context)));
        });
        d.declareFunction("cos", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.cos(asDouble(a.eval(context)));
        });
        return d;
    }

    public static double asDouble(Object any){
        return NTxValue.of(any).asDouble().orElse(0.0);
    }
}

package net.thevpc.ntexup.extension.plot2d.expr;

import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.nuts.expr.*;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.util.List;

public class NTxExprHelper {
    public static NExprMutableDeclarations create(NTxNodeRendererContext renderContext){
        NExprMutableDeclarations d = NExprs.of().newMutableDeclarations();
        d.declareConstant("pi",Math.PI);
        d.declareConstant("PI",Math.PI);
        d.declareConstant("π",Math.PI);
        d.declareConstant("C",299792458.0);
        d.declareConstant("ε",8.85418782E-12);
        d.declareConstant("μ",1.25663706E-6);
        d.declareFunction("sin", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.sin(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("cos", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.cos(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("abs", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.abs(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("tan", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.tan(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("tanh", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.tanh(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("sinh", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.sinh(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("cosh", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.cosh(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("signum", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.signum(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("ulp", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.ulp(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("asin", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.asin(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("acos", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.acos(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("toRadians", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.toRadians(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("toDegrees", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.toDegrees(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("exp", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.exp(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("log", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.log(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("log10", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.log10(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("sqrt", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.sqrt(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("cbrt", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.cbrt(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("ceil", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.ceil(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("floor", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.floor(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("rint", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.rint(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("round", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            return Math.rint(asDouble(a.eval(context),renderContext));
        });
        d.declareFunction("atan2", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            NExprNodeValue b = args.get(1);
            return Math.atan2(
                    asDouble(a.eval(context),renderContext)
                    ,asDouble(b.eval(context),renderContext)
            );
        });
        d.declareFunction("pow", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            NExprNodeValue b = args.get(1);
            return Math.pow(
                    asDouble(a.eval(context),renderContext)
                    ,asDouble(b.eval(context),renderContext)
            );
        });
        d.declareFunction("max", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            NExprNodeValue b = args.get(1);
            return Math.max(
                    asDouble(a.eval(context),renderContext)
                    ,asDouble(b.eval(context),renderContext)
            );
        });
        d.declareFunction("min", (name, args, context) -> {
            NExprNodeValue a = args.get(0);
            NExprNodeValue b = args.get(1);
            return Math.min(
                    asDouble(a.eval(context),renderContext)
                    ,asDouble(b.eval(context),renderContext)
            );
        });
        return d;
    }

    public static double asDouble(NOptional<Object>  any,NTxNodeRendererContext renderContext){
        if(any.isError()){
            renderContext.log().log(NMsg.ofC("evaluation error : %s",any.getMessage().get()));
        }
        return NTxValue.of(any.orNull()).asDoubleOrNumber().orElse(0.0);
    }
}

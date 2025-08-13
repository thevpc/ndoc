package net.thevpc.ntexup.engine.base.functions.general;

import net.thevpc.ntexup.api.eval.NTxFunctionArg;
import net.thevpc.ntexup.api.eval.NTxFunctionArgs;
import net.thevpc.ntexup.api.eval.NTxFunctionContext;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.api.extension.NTxFunction;
import net.thevpc.ntexup.api.util.NTxNumberUtils;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

public class NTxFunctionDtimes implements NTxFunction {
    @Override
    public String name() {
        return "dtimes";
    }

    @Override
    public NElement invoke(NTxFunctionArgs args, NTxFunctionContext context) {
        List<String> all = new ArrayList<>();
        NTxFunctionArg[] argsed = args.args();
        if(argsed.length==0){
            return NElement.ofArray();
        }
        if(argsed.length==1){
            return NElement.ofArray(args.arg(0).eval());
        }
        if(argsed.length==2){
            Double min = NTxValue.of(args.arg(0).eval()).asDouble().orNull();
            if(min==null){
                return NElement.ofArray();
            }
            Double max = NTxValue.of(args.arg(1).eval()).asDouble().orNull();
            if(max==null){
                return NElement.ofArray();
            }
            return NElement.ofDoubleArray(
                    NTxNumberUtils.dtimes(
                            min,
                            max,
                            1
                    )
            );
        }
        Double min = NTxValue.of(args.arg(0).eval()).asDouble().orNull();
        if(min==null){
            return NElement.ofArray();
        }
        Double max = NTxValue.of(args.arg(1).eval()).asDouble().orNull();
        if(max==null){
            return NElement.ofArray();
        }
        Integer s = NTxValue.of(args.arg(2).eval()).asInt().orNull();
        if(s==null){
            s=2;
        }
        return NElement.ofDoubleArray(
                NTxNumberUtils.dtimes(
                        min,
                        max,
                        s
                )
        );
    }


}

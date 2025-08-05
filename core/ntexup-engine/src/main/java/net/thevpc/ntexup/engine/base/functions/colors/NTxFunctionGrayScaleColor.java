package net.thevpc.ntexup.engine.base.functions.colors;

import net.thevpc.ntexup.api.extension.NTxFunction;
import net.thevpc.ntexup.api.eval.NTxFunctionArgs;
import net.thevpc.ntexup.api.eval.NTxFunctionContext;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.engine.util.NTxColorUtils;
import net.thevpc.ntexup.engine.util.NTxElementUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;

public class NTxFunctionGrayScaleColor implements NTxFunction {
    @Override
    public String name() {
        return "grayScaleColor";
    }

    @Override
    public NElement invoke(NTxFunctionArgs args, NTxFunctionContext context) {
        if (args.size() == 0) {
            return NElement.ofNull();
        }
        if (args.size() > 1) {
            context.messages().log(NMsg.ofC("%s: expected 1 argument, got %s",name(), args.size()));
        }
        Color c = NTxValue.of(args.eval(0)).asColor().get();
        return NTxElementUtils.toElement(NTxColorUtils.grayscale(c));
    }
}

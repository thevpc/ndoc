package net.thevpc.ntexup.engine.base.functions.colors;

import net.thevpc.ntexup.api.eval.NDocFunctionArgs;
import net.thevpc.ntexup.api.eval.NDocFunctionContext;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.ntexup.api.extension.NTxFunction;
import net.thevpc.ntexup.engine.util.NDocColorUtils;
import net.thevpc.ntexup.engine.util.NDocElementUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;

public class NTxFunctionLighterColor implements NTxFunction {
    @Override
    public String name() {
        return "lighterColor";
    }

    @Override
    public NElement invoke(NDocFunctionArgs args, NDocFunctionContext context) {
        if (args.size() == 0) {
            return NElement.ofNull();
        }
        Color c = NDocValue.of(args.eval(0)).asColor().get();
        if (args.size() == 1) {
            return NDocElementUtils.toElement(NDocColorUtils.lighter(c));
        }
        if (args.size() > 2) {
            context.messages().log(NMsg.ofC("%s: expected 2 arguments, got %s", name(), args.size()));
        }
        float degrees = NDocValue.of(args.eval(1)).asFloat().get();
        return NDocElementUtils.toElement(NDocColorUtils.lighter(c, degrees));
    }
}

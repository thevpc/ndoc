package net.thevpc.ntexup.engine.base.functions.colors;

import net.thevpc.ntexup.api.eval.*;
import net.thevpc.ntexup.api.extension.NTxFunction;
import net.thevpc.ntexup.engine.util.NDocElementUtils;
import net.thevpc.ntexup.engine.util.NDocColorUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;

public class NTxFunctionInvertColor implements NTxFunction {
    @Override
    public String name() {
        return "invertColor";
    }

    @Override
    public NElement invoke(NDocFunctionArgs args, NDocFunctionContext context) {
        if (args.size() == 0) {
            return NElement.ofNull();
        }
        if (args.size() > 1) {
            context.messages().log(NMsg.ofC("%s: expected 1 argument, got %s",name(), args.size()));
        }
        Color c = NDocValue.of(args.eval(0)).asColor().get();
        return NDocElementUtils.toElement(NDocColorUtils.invert(c));
    }
}

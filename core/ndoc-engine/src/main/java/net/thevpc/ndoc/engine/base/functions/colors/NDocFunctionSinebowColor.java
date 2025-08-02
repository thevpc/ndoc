package net.thevpc.ndoc.engine.base.functions.colors;

import net.thevpc.ndoc.api.eval.NDocFunctionArgs;
import net.thevpc.ndoc.api.eval.NDocFunctionContext;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.extension.NDocFunction;
import net.thevpc.ndoc.engine.util.NDocColorUtils;
import net.thevpc.ndoc.engine.util.NDocElementUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

public class NDocFunctionSinebowColor implements NDocFunction {
    @Override
    public String name() {
        return "sinebowColor";
    }

    @Override
    public NElement invoke(NDocFunctionArgs args, NDocFunctionContext context) {
        if (args.size() == 0) {
            return NElement.ofNull();
        }
        if (args.size() > 1) {
            context.messages().log(NMsg.ofC("%s: expected 1 argument, got %s", name(), args.size()));
        }
        float c = NDocValue.of(args.eval(0)).asFloat().get();
        return NDocElementUtils.toElement(NDocColorUtils.sinebow(c));
    }
}

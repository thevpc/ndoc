package net.thevpc.ntexup.engine.base.functions.colors;

import net.thevpc.ntexup.api.eval.NDocFunctionArgs;
import net.thevpc.ntexup.api.eval.NDocFunctionContext;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.ntexup.api.extension.NTxFunction;
import net.thevpc.ntexup.engine.util.NDocColorUtils;
import net.thevpc.ntexup.engine.util.NDocElementUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

public class NTxFunctionCubehelixColor implements NTxFunction {
    @Override
    public String name() {
        return "cubehelixColor";
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
        return NDocElementUtils.toElement(NDocColorUtils.cubehelix(c));
    }
}

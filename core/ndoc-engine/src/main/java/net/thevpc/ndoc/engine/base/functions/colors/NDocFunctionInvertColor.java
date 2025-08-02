package net.thevpc.ndoc.engine.base.functions.colors;

import net.thevpc.ndoc.api.eval.*;
import net.thevpc.ndoc.api.extension.NDocFunction;
import net.thevpc.ndoc.engine.util.NDocElementUtils;
import net.thevpc.ndoc.engine.util.NDocColorUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;

public class NDocFunctionInvertColor implements NDocFunction {
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

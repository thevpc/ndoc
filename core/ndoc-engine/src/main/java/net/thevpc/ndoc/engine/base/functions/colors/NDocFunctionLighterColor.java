package net.thevpc.ndoc.engine.base.functions.colors;

import net.thevpc.ndoc.api.eval.NDocFunctionArgs;
import net.thevpc.ndoc.api.eval.NDocFunctionContext;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.extension.NDocFunction;
import net.thevpc.ndoc.engine.util.NDocColorUtils;
import net.thevpc.ndoc.engine.util.NDocElementUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

import java.awt.*;

public class NDocFunctionLighterColor implements NDocFunction {
    @Override
    public String name() {
        return "darkerColor";
    }

    @Override
    public NElement invoke(NDocFunctionArgs args, NDocFunctionContext context) {
        if (args.size() == 0) {
            return NElement.ofNull();
        }
        Color c = NDocValue.of(args.eval(0)).asColor().get();
        if (args.size() == 1) {
            return NDocElementUtils.toElement(NDocColorUtils.darker(c));
        }
        if (args.size() > 2) {
            context.messages().log(NMsg.ofC("%s: expected 2 arguments, got %s", name(), args.size()));
        }
        float degrees = NDocValue.of(args.eval(1)).asFloat().get();
        return NDocElementUtils.toElement(NDocColorUtils.rotateSinebowDegrees(c, degrees));
    }
}

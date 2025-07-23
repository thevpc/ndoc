package net.thevpc.ndoc.engine.eval.fct;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.eval.NDocFunction;
import net.thevpc.ndoc.api.eval.NDocFunctionArg;
import net.thevpc.ndoc.api.eval.NDocFunctionContext;
import net.thevpc.nuts.elem.NArrayElementBuilder;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

import java.util.Collections;
import java.util.List;

public class NDocArrayRotateFunction implements NDocFunction {
    @Override
    public String name() {
        return "arrayRotate";
    }

    @Override
    public NElement invoke(NDocFunctionArg[] args, NDocFunctionContext context) {
        if (args.length == 2) {
            NElement u = args[0].get();
            NElement i = args[1].get();
            if (u.isArray() && i.isNumber()) {
                int iv = i.asNumberValue().get().intValue();
                if (iv == 0) {
                    return u;
                }
                NArrayElementBuilder b = u.asArray().get().builder();
                List<NElement> children = u.asArray().get().children();
                if (children.isEmpty()) {
                    return u;
                }
                Collections.rotate(children, iv);
                b.setChildren(children);
                return b.build();
            }
            context.messages().log(NDocMsg.of(NMsg.ofC("unable to rotate %s with distance %s", u, i)));
            return u;
        } else {
            if (args.length > 1) {
                NElement u = args[0].get();
                context.messages().log(NDocMsg.of(NMsg.ofC("arrayRotate: expected 2 arguments, got %s", args.length)));
                return u;
            }
        }

        return NElement.ofNull();
    }
}

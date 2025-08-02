package net.thevpc.ndoc.engine.eval.fct.general;

import net.thevpc.ndoc.api.eval.*;
import net.thevpc.nuts.elem.NArrayElementBuilder;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

import java.util.Collections;
import java.util.List;

public class NDocFunctionArrayLeftRotate implements NDocFunction {
    @Override
    public String name() {
        return "arrayLeftRotate";
    }

    @Override
    public NElement invoke(NDocFunctionArgs args, NDocFunctionContext context) {
        if (args.size() == 0) {
            return NElement.ofNull();
        }
        if (args.size() == 1) {
            return args.eval(0);
        }
        if (args.size() > 2) {
            context.messages().log(NMsg.ofC("%s: expected 2 arguments, got %s",name(), args.size()));
        }
        NElement u = args.eval(0);
        NElement i = args.eval(1);
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
            Collections.rotate(children, -iv);
            b.setChildren(children);
            return b.build();
        }
        context.messages().log(NMsg.ofC("unable to left rotate %s with distance %s", u, i));
        return u;
    }
}

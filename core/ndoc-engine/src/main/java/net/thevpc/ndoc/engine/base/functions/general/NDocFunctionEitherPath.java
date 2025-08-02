package net.thevpc.ndoc.engine.eval.fct.general;

import net.thevpc.ndoc.api.eval.NDocFunction;
import net.thevpc.ndoc.api.eval.NDocFunctionArg;
import net.thevpc.ndoc.api.eval.NDocFunctionArgs;
import net.thevpc.ndoc.api.eval.NDocFunctionContext;
import net.thevpc.ndoc.engine.eval.GitHelper;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;

import java.util.ArrayList;
import java.util.List;

public class NDocFunctionEitherPath implements NDocFunction {
    @Override
    public String name() {
        return "eitherPath";
    }

    @Override
    public NElement invoke(NDocFunctionArgs args, NDocFunctionContext context) {
        List<NElement> possibilities = new ArrayList<>();
        for (NDocFunctionArg arg : args.args()) {
            NElement u = arg.eval();
            possibilities.add(u);
            if (!NBlankable.isBlank(u)) {
                String sp = u.asStringValue().orNull();
                if (!NBlankable.isBlank(u)) {
                    if (GitHelper.isGithubFolder(sp)) {
                        try {
                            NPath pp = GitHelper.resolveGithubPath(sp, context.messages());
                            return NElement.ofString(pp.toString());
                        } catch (Exception ex) {
                            //just ignore
                        }
                    } else {
                        NPath p = NPath.of(sp);
                        if (p.isAbsolute()) {
                            if (p.exists()) {
                                return u;
                            }
                        } else {
                            //should we not get some engine context?
                            if (p.exists()) {
                                return u;
                            }
                        }
                    }
                    return u;
                }
            }
        }
        for (NElement u : possibilities) {
            if (!NBlankable.isBlank(u)) {
                return u;
            }
        }
        for (NElement u : possibilities) {
            return u;
        }
        return NElement.ofNull();
    }
}

package net.thevpc.ndoc.engine.eval;

import net.thevpc.ndoc.api.eval.NDocVar;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.util.function.Supplier;

public class NDocVarImpl implements NDocVar {
    private Supplier<NElement>e;

    public static NOptional<NDocVar> ofOptional(String name, Supplier<NElement> e) {
        return NOptional.ofNamed(new NDocVarImpl(e),"var "+name);
    }

    public NDocVarImpl(Supplier<NElement> e) {
        this.e = e;
    }

    @Override
    public NElement get() {
        return e.get();
    }
}
